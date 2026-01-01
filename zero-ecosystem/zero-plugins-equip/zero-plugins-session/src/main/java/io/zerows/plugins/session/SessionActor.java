package io.zerows.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 会话管理的核心 Actor
 * <pre>
 *     HActor ---> SessionActor
 *                      |------> SessionClientManager
 *                      |------> SessionClient -> ( DI 提取 )
 * </pre>
 *
 * @author lang : 2025-10-13
 */
@Actor(value = "SESSION", configured = false)
@Slf4j
public class SessionActor extends AbstractHActor {
    private static final SessionManager MANAGER = SessionManager.of();

    /**
     * 基本属性参考：<a href="https://vertx.io/blog/writing-secure-vert-x-web-apps/">Secure Session</a>
     *
     * @param vertx Vertx引用
     *
     * @return SessionHandler
     */
    public static Future<SessionHandler> waitHandler(final Vertx vertx) {
        return waitStore(vertx).compose(store -> {
            final SessionHandler handler = SessionHandler.create(store)
                .setCookieHttpOnlyFlag(true);
            return Future.succeededFuture(handler);
        });
    }

    public static Future<SessionStore> waitStore(final Vertx vertx) {
        Objects.requireNonNull(vertx);
        final String key = SessionUtil.keyOf(vertx);
        return MANAGER.getOrCreate(key, () -> SessionUtil.createStore(vertx));
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<SessionClient> addOn = SessionAddOn.of(vertxRef, config);
        this.vLog("[ Session ] SessionActor 初始化完成，配置：{}", config);


        final Provider<SessionClient> provider = new SessionProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Session ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
