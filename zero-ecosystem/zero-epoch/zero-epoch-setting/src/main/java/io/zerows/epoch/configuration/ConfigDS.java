package io.zerows.epoch.configuration;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmDataSource;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Slf4j
public class ConfigDS extends ConfigNorm {

    private final ConcurrentMap<String, HConfig> config = new ConcurrentHashMap<>();

    private String master;

    private boolean strict = Boolean.FALSE;

    private boolean dynamic = Boolean.FALSE;

    private void putAll(final ConcurrentMap<String, HConfig> configMap) {
        this.config.clear();
        this.config.putAll(configMap);
    }

    private Database get(final String key) {
        if (Ut.isNil(key)) {
            return null;
        }
        final HConfig config = this.config.get(key);
        if (Objects.isNull(config)) {
            return null;
        }
        return config.ref();
    }

    static ConfigDS of(final YmDataSource datasource) {
        Objects.requireNonNull(datasource, "[ ZERO ] 数据源配置不可为空！");
        final ConfigDS config = new ConfigDS();
        // 是否存在多个库的情况
        final YmDataSource.Dynamic dynamic = datasource.getDynamic();
        final Database masterDatabase;
        if (Objects.nonNull(dynamic)) {
            // 无动态配置，只能是静态的，此时 config 是空的
            config.dynamic(true);
            config.strict(dynamic.isStrict());
            final Map<String, Database> databaseMap = dynamic.getDatasource();
            // 多库处理
            final ConcurrentMap<String, HConfig> databaseConfigMap = new ConcurrentHashMap<>();
            for (final String field : databaseMap.keySet()) {
                final Database database = databaseMap.get(field);
                if (Objects.isNull(database)) {
                    log.warn("[ ZERO ] 提取的数据源为空：{}", field);
                }
                // DBCP - 初始化连接池
                configPool(database, datasource);

                databaseConfigMap.put(field, new ConfigNorm().putRef(database));
            }
            config.putAll(databaseConfigMap);
            masterDatabase = config.get(dynamic.getPrimary());
            config.master(dynamic.getPrimary());
        } else {
            // 单数据库数据源处理
            final JObject databaseJ = datasource.toJObject();
            masterDatabase = Database.createDatabase(databaseJ);
        }

        Objects.requireNonNull(masterDatabase, "[ ZERO ] 主数据库未配置！");
        // DBCP - 初始化连接池
        configPool(masterDatabase, datasource);

        config.putRef(masterDatabase);
        /*
         * 连接池作为 extension 扩展添加到每个数据库中
         */
        return config;
    }

    private static void configPool(final Database database, final YmDataSource source) {
        Optional.ofNullable(source.getHikari())
            .ifPresent(config -> database.putExtension("hikari", config));
        Optional.ofNullable(source.getTomcat())
            .ifPresent(config -> database.putExtension("tomcat", config));
        Optional.ofNullable(source.getDbcp2())
            .ifPresent(config -> database.putExtension("dbcp2", config));
        Optional.ofNullable(source.getUserCp())
            .ifPresent(config -> database.putExtension("user-cp", config));
        final String configured = database.findNameOfDBCP();
        if (StrUtil.isEmpty(configured)) {
            // 默认 hikari
            database.putExtension("hikari", new JsonObject());
            log.debug("[ ZERO ] 切换到默认数据源：hikari 连接池！database = `{}`", database.getInstance());
        } else {
            log.debug("[ ZERO ] 选择数据源连接池：{}！/ database = `{}`", configured, database.getInstance());
        }
    }
}
