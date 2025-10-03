package io.zerows.extension.commerce.rbac.agent.service.login.pre;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.zerows.epoch.corpus.Ux;
import io.zerows.exception.web._60050Exception501NotSupport;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.exception._80222Exception401ImageCodeWrong;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;
import io.zerows.extension.commerce.rbac.util.Sc;

import java.util.Objects;

/**
 * @author lang : 2024-09-16
 */
public class ImageService implements ImageStub {

    private static final ScConfig CONFIG = ScPin.getConfig();

    private final ScClock<String> cache;

    public ImageService() {
        this.cache = ClockFactory.ofImage(this.getClass());
    }

    @Override
    public Future<Buffer> generate(final String session) {
        final Boolean support = CONFIG.getSupportCaptcha();
        if (Objects.isNull(support) || !support) {
            // 图片验证码功能未开启
            return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
        }
        // 生成图片验证码
        final String imageCode = this.cache.generate();

        // 保存图片验证码到限时缓存中
        return this.cache.put(session, imageCode)
            // 根据 CONFIG 中配置生成图片验证码
            .compose(Sc::imageGenerated);
    }

    @Override
    public Future<Boolean> verify(final String session, final String imageCode) {
        final Boolean support = CONFIG.getSupportCaptcha();
        if (Objects.isNull(support) || !support) {
            // 未启用图片验证码功能，直接返回 true，可执行后续步骤
            return Ux.futureT();
        }


        Objects.requireNonNull(session);
        if (Objects.isNull(imageCode)) {
            // 输入验证码为空
            return FnVertx.failOut(_80222Exception401ImageCodeWrong.class, (Object) null);
        }

        return this.cache.get(session, false).compose(stored -> {
            // 存储的验证码为空
            if (Objects.isNull(stored)) {
                return FnVertx.failOut(_80222Exception401ImageCodeWrong.class, imageCode);
            }


            // 大小写敏感去掉
            if (!stored.equalsIgnoreCase(imageCode)) {
                return FnVertx.failOut(_80222Exception401ImageCodeWrong.class, imageCode);
            }
            return Ux.futureT();
        });
    }
}
