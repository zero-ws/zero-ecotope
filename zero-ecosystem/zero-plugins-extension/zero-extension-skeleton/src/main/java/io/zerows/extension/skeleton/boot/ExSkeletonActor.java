package io.zerows.extension.skeleton.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;

/**
 * Extension 扩展框架的启动骨架，启动之后可打印扩展框架的启动详细信息
 *
 * @author lang : 2025-10-31
 */
@Actor(value = "extension", sequence = 1)
public class ExSkeletonActor extends ExAbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {

        return Future.succeededFuture(Boolean.TRUE);
    }
}
