package io.zerows.plugins.session;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * 内部会话客户端管理器
 *
 * @author lang : 2025-10-14
 */
class SessionManager extends AddOnManager<SessionClient> {
    private static final Cc<String, SessionClient> CC_STORED = Cc.open();

    private static final SessionManager INSTANCE = new SessionManager();
    private final SessionStoreManager storeManager;

    private SessionManager() {
        this.storeManager = new SessionStoreManager();
    }

    static SessionManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, SessionClient> stored() {
        return CC_STORED;
    }

    // --------------------------------------------------------------------------------

    /**
     * 二级对象，同样的一个 {@link AddOnManager}，但是管理的对象变成了 {@link SessionStore}。
     *
     * @return SessionStore 管理器
     */
    public SessionStoreManager STORE() {
        return this.storeManager;
    }

    public static class SessionStoreManager extends AddOnManager<SessionStore> {
        /**
         * 此处的 {@link SessionStore} 的具体限制在于，每一个 {@link Vertx} 实例只能选择一种方式的存储，不可以交叉，一旦交叉会导致
         * Session 在处理流程中无法提取到唯一的 {@link SessionHandler} 而引起会话管理异常
         * <pre>
         *     1. 默认场景使用 {@link LocalSessionStore}
         *     2. 分布式场景使用 {@link ClusteredSessionStore}
         *     3. 自定义模式下可使用其他方式构造，如 Redis / Mongo 等
         * </pre>
         */
        private static final Cc<String, SessionStore> CC_STORED = Cc.open();

        private SessionStoreManager() {
        }

        @Override
        protected Cc<String, SessionStore> stored() {
            return CC_STORED;
        }
    }
}
