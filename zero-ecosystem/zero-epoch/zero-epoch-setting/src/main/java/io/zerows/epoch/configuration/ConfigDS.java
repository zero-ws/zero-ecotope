package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmDataSource;
import io.zerows.platform.metadata.KDatabase;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
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

    private KDatabase get(final String key) {
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
        final KDatabase masterDatabase;
        if (Objects.nonNull(dynamic)) {
            // 无动态配置，只能是静态的，此时 config 是空的
            config.dynamic(true);
            config.strict(dynamic.isStrict());
            final Map<String, KDatabase> databaseMap = dynamic.getDatasource();
            // 多库处理
            final ConcurrentMap<String, HConfig> databaseConfigMap = new ConcurrentHashMap<>();
            for (final String field : databaseMap.keySet()) {
                final KDatabase database = databaseMap.get(field);
                if (Objects.isNull(database)) {
                    log.warn("[ ZERO ] 提取的数据源为空：{}", field);
                }
                databaseConfigMap.put(field, new ConfigNorm().putRef(database));
            }
            config.putAll(databaseConfigMap);
            masterDatabase = config.get(dynamic.getPrimary());
            config.master(dynamic.getPrimary());
        } else {
            // 单数据库数据源处理
            final JsonObject databaseJ = datasource.toJson();
            final KDatabase database = new KDatabase();
            database.fromJson(databaseJ);
            masterDatabase = database;
        }
        final JsonObject hikari = datasource.getHikari();
        config.putOptions("hikari", hikari);
        config.putRef(masterDatabase);
        return config;
    }
}
