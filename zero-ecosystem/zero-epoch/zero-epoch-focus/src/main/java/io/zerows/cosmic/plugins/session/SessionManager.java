package io.zerows.cosmic.plugins.session;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

import java.util.function.Supplier;

/**
 * 内部会话客户端管理器
 *
 * @author lang : 2025-10-14
 */
class SessionManager {
    private static final Cc<String, SessionClient> CC_INSTANCE = Cc.open();
    /**
     * 此处的 {@link SessionStore} 的具体限制在于，每一个 {@link Vertx} 实例只能选择一种方式的存储，不可以交叉，一旦交叉会导致
     * Session 在处理流程中无法提取到唯一的 {@link SessionHandler} 而引起会话管理异常
     * <pre>
     *     1. 默认场景使用 {@link LocalSessionStore}
     *     2. 分布式场景使用 {@link ClusteredSessionStore}
     *     3. 自定义模式下可使用其他方式构造，如 Redis / Mongo 等
     * </pre>
     */
    private static final Cc<Integer, SessionStore> CC_STORE = Cc.open();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    static SessionManager of() {
        return INSTANCE;
    }

    public SessionManager putClient(final String name, final SessionClient client) {
        CC_INSTANCE.put(name, client);
        return this;
    }

    public SessionManager removeClient(final String name) {
        CC_INSTANCE.remove(name);
        return this;
    }

    public SessionClient getClient(final String name) {
        return CC_INSTANCE.get(name);
    }

    public SessionClient getClient(final String name, final Supplier<SessionClient> clientSupplier) {
        return CC_INSTANCE.pick(clientSupplier, name);
    }

    public SessionManager putStore(final Vertx vertx, final SessionStore store) {
        CC_STORE.put(vertx.hashCode(), store);
        return this;
    }

    public SessionManager removeStore(final Vertx vertx) {
        CC_STORE.remove(vertx.hashCode());
        return this;
    }

    public SessionStore getStore(final Vertx vertx) {
        return CC_STORE.get(vertx.hashCode());
    }

    public SessionStore getStore(final Vertx vertx, final Supplier<SessionStore> storeSupplier) {
        return CC_STORE.pick(storeSupplier, vertx.hashCode());
    }
}
