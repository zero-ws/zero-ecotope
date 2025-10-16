package io.zerows.cosmic.plugins.job.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-10-16
 */
@Slf4j
public class JobClientActor extends AbstractHActor {
    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {

        return Future.succeededFuture(Boolean.TRUE);
    }
}
