package io.zerows.plugins.flyway;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;

import java.util.Objects;

/**
 * @author lang : 2025-10-25
 */
@Actor(value = "flyway", sequence = -216)
@Slf4j
public class FlywayActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final boolean disabled = config.options(FlywayKeys.DISABLED, false);
        if (disabled) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        // 配置信息
        final FluentConfiguration configuration = Flyway11Configurator.from(config);
        final Flyway flyway = new Flyway(configuration);
        final MigrateResult result = Fn.jvmOr(flyway::migrate);
        if (Objects.nonNull(result)) {
            log.info("[ PLUG ] Flyway数据库迁移结果：{}", result.getTotalMigrationTime());
        } else {
            log.warn("[ PLUG ] Flyway数据库迁移执行失败！");
        }
        return Future.succeededFuture(Boolean.TRUE);
    }
}
