package io.zerows.plugins.security.service;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.epoch.assembly.DiFactory;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * 前置服务，可直接查找，核心SPI，用于特殊前置场景
 * <pre>
 *     - Email 发邮件
 *     - SMS 发短信
 *     - OTP 模式的前序步骤
 * </pre>
 */
public interface AsyncPreAuth {
    Cc<String, AsyncPreAuth> CC_SKELETON = Cc.openThread();

    static AsyncPreAuth of(final TypeLogin typeLogin) {
        final String nameSPI = "PreAuth/" + typeLogin.name();
        return CC_SKELETON.pick(() -> {
            // SPI
            final AsyncPreAuth authService = HPI.findOne(AsyncPreAuth.class, nameSPI);
            if (Objects.nonNull(authService)) {
                // DI
                final Injector injector = DiFactory.singleton().build();
                injector.injectMembers(authService);
            }
            return authService;
        }, nameSPI);
    }

    Future<Kv<String, String>> authorize(String identifier);
}
