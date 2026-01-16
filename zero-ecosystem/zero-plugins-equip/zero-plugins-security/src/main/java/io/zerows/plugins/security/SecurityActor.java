package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.plugins.security.service.TokenBuilderAES;
import io.zerows.plugins.security.service.TokenBuilderBasic;
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
@Actor(value = "security", sequence = -160)
public class SecurityActor extends AbstractHActor {

    // ------------ Actor 提供的静态方法 ----------------
    public static SecurityConfig configOf(final SecurityType type) {
        return manager().configOf(type);
    }

    public static SecurityConfig configJwt() {
        return manager().configJwt();
    }

    public static SecurityCaptcha configCaptcha() {
        return manager().configCaptcha();
    }

    public static YmSecurity configuration() {
        return manager().configuration();
    }

    private static SecurityManager manager() {
        return SecurityManager.of();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("[ Security ] SecurityActor 初始化完成，配置：{}", config);
        // 先注册配置信息
        manager().registryOf(config, vertxRef);

        TokenBuilderManager.of().registry(TokenType.BASIC, TokenBuilderBasic::new);
        TokenBuilderManager.of().registry(TokenType.AES, TokenBuilderAES::new);

        // 扫描 @Wall 的所有元数据信息，用于后期直接挂载到路由中去
        SecurityContext.scanned(vertxRef);
        // 填充构造 Lee 的的核心信息
        return Future.succeededFuture(Boolean.TRUE);
    }
}
