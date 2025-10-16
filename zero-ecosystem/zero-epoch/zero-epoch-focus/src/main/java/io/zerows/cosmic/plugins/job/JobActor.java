package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.cosmic.plugins.job.management.ORepositoryJob;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.management.ORepository;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "job", sequence = -2)
@Slf4j
public class JobActor extends AbstractHActor {
    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("[ Job ] JobActor 正在扫描任务类，vertx = {}", vertxRef.hashCode());

        final HSetting setting = NodeStore.ofSetting(vertxRef);
        ORepository.ofOr(ORepositoryJob.class).whenStart(setting);

        this.vLog("[ Job ] ✅ JobActor 已成功扫描完成！！");

        return Future.succeededFuture(Boolean.TRUE);
    }
}
