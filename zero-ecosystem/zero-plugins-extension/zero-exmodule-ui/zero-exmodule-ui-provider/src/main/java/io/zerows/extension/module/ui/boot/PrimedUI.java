package io.zerows.extension.module.ui.boot;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.extension.skeleton.underway.Primed;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-12-25
 */
@SPID(priority = 1024)
public class PrimedUI implements Primed {
    private static final MDUIManager MANAGER = MDUIManager.of();

    @Override
    public Future<Boolean> afterAsync(final Set<MDConfiguration> waitSet, final Vertx vertxRef) {
        waitSet.forEach(configuration -> {
            // 懒加载列信息
            final Set<MDEntity> entities = configuration.inEntity();
            entities.stream().filter(Objects::nonNull)
                .filter(entity -> Objects.nonNull(entity.identifier()))
                .forEach(entity -> {
                    final String identifier = entity.identifier();
                    final JsonArray columns = entity.inColumns();
                    // 后续处理流程
                    MANAGER.handleAfter(identifier, columns);
                });
        });
        return Future.succeededFuture(Boolean.TRUE);
    }
}
