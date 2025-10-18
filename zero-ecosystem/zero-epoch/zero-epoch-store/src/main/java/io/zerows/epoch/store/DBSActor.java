package io.zerows.epoch.store;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.ConfigDS;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "DATABASE", sequence = -1017)
@Slf4j
public class DBSActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (!(config instanceof final ConfigDS configDs)) {
            this.vLog("[ DBS ] DBSActor 因为配置异常跳过：{}", config);
            throw new _501NotSupportException("[ ZERO ] 数据库配置类型错误！");
        }

        this.vLog("[ DBS ] DBSActor 初始化数据源！");
        this.configure(configDs);

        final DBSAddOn addOn = DBSAddOn.of(vertxRef, config);

        return Future.succeededFuture(Boolean.TRUE);
    }

    public void configure(final ConfigDS configDs) {
        try {
            if (configDs.dynamic()) {
                // 动态配置数据库
                configDs.config().forEach((key, config) -> {
                    final Database database = config.ref();
                    DBMany.of().put(key, database);
                    this.vLog("[ DBS ] 数据库 key = {} 初始化完成，类型：{}", key, database.getType());
                });
            } else {
                // 只有 Master 库
                DBMany.of().put("master", configDs.ref());
            }
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static DBS ofDBS() {
        return DBMany.of().get();
    }

    public static Database ofDatabase() {
        return ofDBS().getDatabase();
    }

    public static DataSource ofDataSource() {
        return ofDBS().getDs();
    }
}
