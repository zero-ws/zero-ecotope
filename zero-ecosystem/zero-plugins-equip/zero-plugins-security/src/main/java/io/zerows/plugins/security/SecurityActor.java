package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.specification.configuration.HConfig;

/**
 * 此处两个组件的职责
 * <pre>
 *     1. {@link SecurityManager} 负责管理配置数据
 *     2. {@link SecurityContext} 负责管理运行时的安全上下文
 * </pre>
 *
 * @author lang : 2025-10-27
 */
@Actor(value = "security")
public class SecurityActor extends AbstractHActor {
    private static final SecurityManager MANAGER = SecurityManager.of();

    // ------------ Actor 提供的静态方法 ----------------
    public static SecurityConfig configOf(final SecurityType type) {
        return MANAGER.configOf(type);
    }

    public static SecurityConfig configJwt() {
        return MANAGER.configJwt();
    }

    public static SecurityCaptcha configCaptcha() {
        return MANAGER.configCaptcha();
    }

    public static YmSecurity configuration() {
        return MANAGER.configuration();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("[ Security ] SecurityActor 初始化完成，配置：{}", config);
        // 先注册配置信息
        MANAGER.registryOf(config, vertxRef);

        // 扫描 @Wall 的所有元数据信息，用于后期直接挂载到路由中去
        SecurityContext.scanned(vertxRef);

        // 填充构造 Lee 的的核心信息
        return Future.succeededFuture(Boolean.TRUE);
    }
}
