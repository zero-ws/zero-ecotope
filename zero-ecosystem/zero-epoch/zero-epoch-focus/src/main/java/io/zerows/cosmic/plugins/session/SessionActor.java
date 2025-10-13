package io.zerows.cosmic.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;

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
public class SessionActor implements HActor {
    @Override
    public Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return null;
    }
}
