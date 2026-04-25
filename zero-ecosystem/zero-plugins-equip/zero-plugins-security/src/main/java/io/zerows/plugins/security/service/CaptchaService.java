package io.zerows.plugins.security.service;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.exception._80200Exception401CaptchaWrong;
import io.zerows.plugins.security.exception._80201Exception401CaptchaExpired;
import io.zerows.plugins.security.exception._80212Exception500CaptchaDisabled;
import io.zerows.plugins.security.exception._80213Exception500CaptchaGeneration;
import io.zerows.plugins.security.exception._80242Exception400CaptchaRequired;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.plugins.security.metadata.YmSecurityCaptcha;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@Slf4j
public class CaptchaService implements CaptchaStub {
    // 定义内部 Map Key 常量，防止魔法值
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_IMAGE = "image";

    @Override
    public Future<CaptchaLoginRequest> validateRequired(final CaptchaLoginRequest request) {
        // 安全配置校验
        final YmSecurity security = SecurityActor.configuration();
        if (Objects.isNull(security)) {
            return Ux.future(request);
        }
        if (!security.isCaptcha()) {
            return Ux.future(request);
        }

        // 启用了图片验证码
        if (StrUtil.isEmpty(request.captchaId())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captchaId");
        }
        if (StrUtil.isEmpty(request.captcha())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captcha");
        }
        return Ux.future(request);
    }

    /**
     * <pre>
     * 生成验证码 \uD83C\uDFA8
     *
     * 采用【双模执行策略】生成验证码，平衡 CPU 与 I/O 资源的使用。
     *
     * 1. 物理线程阶段 (Worker Thread) \uD83C\uDFD7️
     *    - 验证码图形生成属于 CPU 密集型任务。
     *    - 使用 {@link io.zerows.program.Ux#waitAsync} 调度到 Worker 线程池执行。
     *    - 避免阻塞 EventLoop 核心线程。
     *
     * 2. 虚拟线程阶段 (Virtual Thread) \uD83D\uDD78️
     *    - Redis/缓存存储属于 I/O 密集型任务。
     *    - 关键约束：{@link io.r2mo.jaas.session.UserCache} 组件使用了 await() 同步转异步机制。
     *    - 旧版！！！！必须使用 {@link io.zerows.program.Ux} 切换到虚拟线程上下文。
     *    - \uD83D\uDEA8 如果在 EventLoop 或普通 Worker 中调用 UserCache，可能导致死锁或线程耗尽。
     *
     * 流程总结：
     * [ Worker ] 图形计算 -> [ Virtual ] 缓存存储 -> [ Response ] 返回结果
     * </pre>
     *
     * @return Future<JsonObject> 包含验证码ID和Base64图像数据
     */
    @Override
    public Future<JsonObject> generate() {
        final CaptchaConfig captcha = SecurityActor.configCaptcha();
        Fn.jvmKo(Objects.isNull(captcha), _80212Exception500CaptchaDisabled.class);
        final YmSecurityCaptcha config = captcha.captchaConfig();

        // 1. 【物理 Worker】重计算 (CPU 密集)
        return Ux.waitAsync(() -> this.execHeavyGeneration(config))
            // 2. 【虚拟线程】Redis 存储 (I/O 密集 + await 调用)
            // 这里必须切换到 Virtual Thread，因为 UserCache.authorize 用了 Future.await()
            .map(result -> {
                final String captchaId = result.get(KEY_ID);
                final Kv<String, String> cacheEntry = Kv.create(captchaId, result.get(KEY_CODE));

                // 这里调用 await() 现在是合法的，因为跑在 Virtual Thread 里
                UserCache.of().authorize(cacheEntry, config.forArguments());

                return new JsonObject()
                    .put(CaptchaRequest.ID, captchaId)
                    .put("image", result.get(KEY_IMAGE));
            });
    }

    /**
     * 纯 CPU 计算逻辑 (运行在 Worker 线程)
     * 负责生成 ID、绘制图形、Base64 编码
     */
    private Map<String, String> execHeavyGeneration(final YmSecurityCaptcha config) {
        final CaptchaConfig captchaConfig = SecurityActor.configCaptcha();
        // A. 生成 ID
        final String captchaId = UUID.randomUUID().toString().replace("-", "");

        // B. 绘制图形 (耗时)
        final LineCaptcha captcha = new ClearLineCaptcha(
            config.getWidth(),
            config.getHeight(),
            captchaConfig,
            Math.max(0, config.getInterfereCount())
        );
        captcha.setGenerator(captchaConfig.captchaGenerator());
        captcha.setFont(captchaConfig.captchaFont());
        if (Objects.nonNull(config.getTextAlpha())) {
            captcha.setTextAlpha(config.getTextAlpha());
        }

        // C. 转换输出 (耗时)
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            captcha.write(out);
            final String base64 = Base64.encode(out.toByteArray());

            // 使用 Map 返回多维数据
            // JDK 9+ 可以用 Map.of，为了兼容性这里用常规写法或 Hutool MapUtil
            // 这里使用 java.util.Map 的标准 put 方式确保通用性
            return Map.of(
                KEY_ID, captchaId,
                KEY_CODE, captcha.getCode(),
                KEY_IMAGE, "data:image/png;base64," + base64
            );
        } catch (final Exception e) {
            log.error("验证码图片生成失败", e);
            throw new _80213Exception500CaptchaGeneration(e.getMessage());
        }
    }

    /**
     * 认证位于主流程，若没有开启则直接校验通过
     */
    @Override
    public Future<Boolean> validate(final String captchaId, final String captcha) {
        final YmSecurity configSecurity = SecurityActor.configuration();
        if (!configSecurity.isCaptcha()) {
            // 未启用验证码，直接通过
            return Future.succeededFuture(Boolean.TRUE);
        }
        // 这里调用 await() 提取 captchaId
        final CaptchaConfig configCaptcha = SecurityActor.configCaptcha();
        final CaptchaArgs arguments = Objects.requireNonNull(configCaptcha.captchaConfig()).forArguments();
        return UserCache.of().authorize(captchaId, arguments).<Future<String>>compose().compose(cached -> {
            if (Objects.isNull(cached)) {
                return Fx.failOut(_80201Exception401CaptchaExpired.class, captchaId, captcha);
            }
            if (!cached.equalsIgnoreCase(captcha)) {
                return Fx.failOut(_80200Exception401CaptchaWrong.class, captcha);
            }
            // 移除验证码 / 认证成功之后再移除
            UserCache.of().authorizeKo(captchaId, arguments);
            return Future.succeededFuture(Boolean.TRUE);
        });

    }

    private static class ClearLineCaptcha extends LineCaptcha {
        private final CaptchaConfig visual;
        private final int configuredInterfereCount;

        ClearLineCaptcha(final int width, final int height, final CaptchaConfig visual, final int interfereCount) {
            super(width, height, visual.captchaGenerator(), interfereCount);
            this.visual = visual;
            this.configuredInterfereCount = interfereCount;
        }

        @Override
        public Image createImage(final String code) {
            final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = ImgUtil.createGraphics(image, this.visual.backgroundColor());
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                this.drawInterfere(g);
                this.drawString(g, code);
            } finally {
                g.dispose();
            }
            return image;
        }

        private void drawInterfere(final Graphics2D g) {
            if (0 >= this.configuredInterfereCount) {
                return;
            }
            final Composite previous = g.getComposite();
            final Float alpha = this.visual.interfereAlpha();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                Math.max(0f, Math.min(1f, alpha))));
            final List<Color> palette = this.visual.interferePalette();
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < this.configuredInterfereCount; i++) {
                final int xs = random.nextInt(this.width);
                final int ys = random.nextInt(this.height);
                final int xe = Math.max(0, Math.min(this.width, xs + random.nextInt(-this.width / 3, this.width / 3 + 1)));
                final int ye = Math.max(0, Math.min(this.height, ys + random.nextInt(-this.height / 2, this.height / 2 + 1)));
                g.setColor(palette.isEmpty() ? this.visual.interfereColor() : palette.get(random.nextInt(palette.size())));
                g.drawLine(xs, ys, xe, ye);
            }
            g.setComposite(previous);
        }

        private void drawString(final Graphics2D g, final String code) {
            if (StrUtil.isBlank(code)) {
                return;
            }
            if (Objects.nonNull(this.textAlpha)) {
                g.setComposite(this.textAlpha);
            }
            g.setFont(this.font);
            final FontMetrics metrics = g.getFontMetrics();
            final int length = code.length();
            final int charWidth = this.width / length;
            final int baseline = (this.height - metrics.getHeight()) / 2 + metrics.getAscent() - 1;
            final List<Color> palette = this.visual.textPalette();
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < length; i++) {
                final String current = String.valueOf(code.charAt(i));
                final int textWidth = metrics.stringWidth(current);
                final int x = i * charWidth + Math.max(2, (charWidth - textWidth) / 2);
                final int y = baseline + random.nextInt(-1, 2);
                g.setColor(palette.isEmpty() ? this.visual.textColor() : palette.get(random.nextInt(palette.size())));
                g.drawString(current, x, y);
            }
        }
    }
}
