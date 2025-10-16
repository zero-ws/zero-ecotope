package io.zerows.cosmic.plugins.session;

import io.r2mo.SourceReflect;
import io.r2mo.function.Fn;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.cosmic.plugins.session.exception._20005Exception500SessionClientInit;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2025-10-14
 */
@Slf4j
class SessionUtil {

    private static final AtomicBoolean LOCKED = new AtomicBoolean(true);
    private static final ConcurrentMap<Integer, AtomicBoolean> LOCKED_MAP = new ConcurrentHashMap<>();

    static SessionStore createStore(final Vertx vertx) {
        final HConfig sessionConfig = NodeStore.ofSession(vertx);
        return createStore(vertx, sessionConfig);
    }

    static String keyOf(final Vertx vertx) {
        return keyOf(vertx, null);
    }

    static String keyOf(final Vertx vertx, final HConfig config) {
        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            return "SESSION-STORE-" + vertx.hashCode();
        } else {
            return "SESSION-STORE-" + vertx.hashCode() + "@" + config.hashCode();
        }
    }

    /**
     * 根据 {@link HConfig} 中的 options 直接构造，这种状况之下配置优先
     *
     * @param config 配置
     *
     * @return SessionStore
     */
    static SessionStore createStore(final Vertx vertx, final HConfig config) {
        /*
         * 此处 config = null 会导致启动失败，所以必须双向检查，未配置的模式下的严格性
         */
        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            // 配置为空，直接用 Vertx 构造
            if (vertx.isClustered()) {
                // （自动）CLUSTER
                return ClusteredSessionStore.create(vertx);
            } else {
                // （自动）LOCAL
                return LocalSessionStore.create(vertx);
            }
        }

        final JsonObject sessionConfig = config.options();
        final String storeType = sessionConfig.getString(YmSpec.vertx.session.store_type);
        final SessionType sessionType = Ut.toEnum(storeType, SessionType.class, SessionType.LOCAL);
        /* 根据 sessionType 来计算 Store 的内置实现 */
        if (LOCKED.getAndSet(Boolean.FALSE)) {
            log.info("[ ZERO ] 会话存储模式为 {}", sessionType);
        }
        if (SessionType.LOCAL == sessionType) {
            // （强制）LOCAL
            return LocalSessionStore.create(vertx);
        } else if (SessionType.CLUSTER == sessionType) {
            // （强制）CLUSTER
            return ClusteredSessionStore.create(vertx);
        } else {
            // DEFINED
            final String storeComponent = sessionConfig.getString(YmSpec.vertx.session.store_component);
            Fn.jvmKo(Ut.isNil(storeComponent), _20005Exception500SessionClientInit.class);

            final AtomicBoolean vertxLocked = LOCKED_MAP.computeIfAbsent(vertx.hashCode(), k -> new AtomicBoolean(true));
            if (vertxLocked.getAndSet(Boolean.FALSE)) {
                log.info("[ ZERO ] 会话存储组件为 {}", storeComponent);
            }

            // 默认场景下这个构造必须是带有 Vertx 参数的
            final SessionStore store = SourceReflect.instance(storeComponent, vertx);
            store.init(vertx, sessionConfig);
            return store;
        }
    }
}
