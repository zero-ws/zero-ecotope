package io.zerows.plugins.security.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.codec.Base64;
import io.r2mo.function.Fn;
import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.SecurityCaptcha;
import io.zerows.plugins.security.exception._80212Exception500CaptchaDisabled;
import io.zerows.plugins.security.exception._80213Exception500CaptchaGeneration;
import io.zerows.plugins.security.metadata.YmSecurityCaptcha;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class CaptchaService implements CaptchaStub {

    private static final SecurityCaptcha CAPTCHA = SecurityActor.configCaptcha();

    // 定义内部 Map Key 常量，防止魔法值
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_IMAGE = "image";

    @Override
    public Future<JsonObject> generate() {
        Fn.jvmKo(Objects.isNull(CAPTCHA), _80212Exception500CaptchaDisabled.class);
        final YmSecurityCaptcha config = CAPTCHA.captchaConfig();

        // 1. 【物理 Worker】重计算 (CPU 密集)
        return Ux.waitAsync(() -> this.execHeavyGeneration(config))
            // 2. 【虚拟线程】Redis 存储 (I/O 密集 + await 调用)
            // 这里必须切换到 Virtual Thread，因为 UserCache.authorize 用了 Future.await()
            .compose(result -> Ux.waitVirtual(() -> {
                final String captchaId = result.get(KEY_ID);
                final Kv<String, String> cacheEntry = Kv.create(captchaId, result.get(KEY_CODE));

                // 这里调用 await() 现在是合法的，因为跑在 Virtual Thread 里
                UserCache.of().authorize(cacheEntry, config.forArguments());

                return new JsonObject()
                    .put(CaptchaRequest.ID, captchaId)
                    .put("image", result.get(KEY_IMAGE));
            }));
    }

    @Override
    public Future<Boolean> validate(final String captchaId, final String captcha) {
        return null;
    }

    /**
     * 纯 CPU 计算逻辑 (运行在 Worker 线程)
     * 负责生成 ID、绘制图形、Base64 编码
     */
    private Map<String, String> execHeavyGeneration(final YmSecurityCaptcha config) {
        // A. 生成 ID
        final String captchaId = UUID.randomUUID().toString().replace("-", "");

        // B. 绘制图形 (耗时)
        final LineCaptcha captcha = CaptchaUtil.createLineCaptcha(
            config.getWidth(),
            config.getHeight()
        );
        captcha.setGenerator(CAPTCHA.captchaGenerator());
        captcha.setFont(CAPTCHA.captchaFont());

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
}