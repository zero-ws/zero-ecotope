package io.zerows.plugins.session;

import io.r2mo.SourceReflect;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.cortex.sdk.AtSession;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.plugins.session.exception._20005Exception500SessionClientInit;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
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
    private static final Cc<String, AtSession> CC_FACTORY = Cc.openThread();

    static Future<SessionStore> createStore(final Vertx vertx) {
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
     * @return SessionStore
     */
    static Future<SessionStore> createStore(final Vertx vertx, final HConfig config) {
        /*
         * 此处 config = null 会导致启动失败，所以必须双向检查，未配置的模式下的严格性
         */
        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            log.info("[ PLUG ] ( Session ) 未配置，默认会话存储模式启动：Cluster = {}", vertx.isClustered());
            // 配置为空，直接用 Vertx 构造
            final SessionStore store;
            if (vertx.isClustered()) {
                // （自动）CLUSTER
                store = ClusteredSessionStore.create(vertx);
            } else {
                // （自动）LOCAL
                store = LocalSessionStore.create(vertx);
            }
            return Future.succeededFuture(store);
        }

        final JsonObject sessionConfig = config.options();
        return createStore(vertx, sessionConfig);
    }

    private static Future<SessionStore> createStore(final Vertx vertx, final JsonObject sessionConfig) {
        final String storeType = sessionConfig.getString(YmSpec.vertx.session.store_type);
        final SessionType sessionType = Ut.toEnum(storeType, SessionType.class, null);


        // 未配置的时候优先考虑 SPI 自动发现
        if (Objects.isNull(sessionType)) {
            final String id = "AtSession/SPI";
            final AtSession factory = CC_FACTORY.pick(() -> HPI.findOne(AtSession.class, id), id);
            if (Objects.nonNull(factory)) {
                final JsonObject sessionOptions = Ut.valueJObject(sessionConfig, YmSpec.vertx.session.options.__);
                if (LOCKED.getAndSet(Boolean.FALSE)) {
                    log.info("[ PLUG ] ( Session ) 会话存储工厂 {}, 配置：{}",
                        factory.getClass().getName(), sessionOptions.encode());
                }
                return factory.createStore(vertx, sessionOptions).compose(store -> {
                    if (Objects.nonNull(store)) {
                        return Future.succeededFuture(store);
                    }
                    return createStore(vertx, sessionConfig, SessionType.LOCAL);
                });
            }
        }

        return createStore(vertx, sessionConfig, sessionType);
    }

    private static Future<SessionStore> createStore(final Vertx vertx, final JsonObject sessionConfig,
                                                    final SessionType sessionType) {
        /*
         * 如果配置了 sessionType，则跳过 SPI 自动发现机制而直接选择固定模式，简单说是为了让开发人员有一个基于优先级的选择
         * 1. 最高优先级，不配置 sessionType
         *    ✅️ 若可以找到则直接使用 SPI 发现的 SessionStore 来实现会话数据存储
         *    ❌️ 若发现不了则直接走固定模式的逻辑
         * 2. 次高优先级
         *    - 配置了无法解析的值：LOCAL
         *    - 配置了 Cluster，取决于 Cluster 的实现，Vertx 官方支持：
         *      Hazelcast、Infinispan、Zookeeper 等等
         *    - 纯自定义模式，必须配置 store_component 来指定组件类
         * */
        if (LOCKED.getAndSet(Boolean.FALSE)) {
            log.info("[ PLUG ] ( Session ) 会话存储模式 = {}", sessionType);
        }
        final SessionStore store;
        if (SessionType.LOCAL == sessionType || Objects.isNull(sessionType)) {
            // （强制）LOCAL
            store = LocalSessionStore.create(vertx);
        } else if (SessionType.CLUSTER == sessionType) {
            // （强制）CLUSTER
            store = ClusteredSessionStore.create(vertx);
        } else {
            // DEFINED
            final String storeComponent = sessionConfig.getString(YmSpec.vertx.session.store_component);
            Fn.jvmKo(Ut.isNil(storeComponent), _20005Exception500SessionClientInit.class);

            final AtomicBoolean vertxLocked = LOCKED_MAP.computeIfAbsent(vertx.hashCode(), k -> new AtomicBoolean(true));
            if (vertxLocked.getAndSet(Boolean.FALSE)) {
                log.info("[ PLUG ] 会话存储组件 {}", storeComponent);
            }

            // 默认场景下这个构造必须是带有 Vertx 参数的
            store = SourceReflect.instance(storeComponent, vertx);
            store.init(vertx, sessionConfig);
        }
        return Future.succeededFuture(store);
    }
}
