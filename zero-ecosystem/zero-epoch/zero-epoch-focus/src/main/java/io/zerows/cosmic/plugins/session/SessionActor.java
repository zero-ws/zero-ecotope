package io.zerows.cosmic.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

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
@Actor("SESSION")
@Slf4j
public class SessionActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        System.out.println(Thread.currentThread().getName());
        return null;
    }
}
