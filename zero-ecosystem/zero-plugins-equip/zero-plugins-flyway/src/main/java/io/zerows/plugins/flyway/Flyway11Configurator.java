package io.zerows.plugins.flyway;

import io.r2mo.base.dbe.DBS;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.DBFlyway;
import io.zerows.epoch.store.DBSActor;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Flyway 11 配置构造器：
 * - 直接从 HConfig.options(key[, default]) 读取强类型配置
 * - 适配 Flyway 11 的 API（ignoreMissing/future → ignoreMigrationPatterns）
 * - 支持：字符串或数组形式的 locations / suffixes / schemas / cherry-pick
 */
@Slf4j
final class Flyway11Configurator {
    private Flyway11Configurator() {
    }

    private static void configure(final FluentConfiguration fc,
                                  final HConfig config) {
        /* ========= 数据源 ========= */
        final String url = config.options(FlywayKeys.URL, null);
        final String user = config.options(FlywayKeys.USER);
        final String pwd = config.options(FlywayKeys.PASSWORD);
        if (Objects.nonNull(url)) {
            fc.dataSource(url, user, pwd);
        } else {
            /*
             * 可指定访问数据源，但目前只能指定一个数据库信息，不可以指定其他数据库信息
             * 1. master-history   历史库不允许开启 flyway
             * 2. master-workflow  工作流库也不允许开启 flyway
             */
            final String database = config.options(FlywayKeys.DATABASE, null);
            final DBS dbs;
            if (Objects.isNull(database)) {
                dbs = DBSActor.ofDBS();
            } else {
                dbs = DBSActor.ofDBS(database);
            }
            fc.dataSource(dbs.getDs());
        }
        // driver-class-name 通常由 JDBC SPI 发现；如需强制，可自行 Class.forName()
    }

    @SuppressWarnings("all")
    static FluentConfiguration from(final HConfig config) {
        final FluentConfiguration fc = Flyway.configure();
        // 数据源单独处理
        configure(fc, config);

        /* ========= 位置 & 编码 ========= */
        final Object value = config.options(FlywayKeys.LOCATIONS);
        final List<String> locations;
        if (value instanceof final JsonArray valueA) {
            locations = valueA.getList();
        } else {
            locations = asStringList(config.options(FlywayKeys.LOCATIONS));
        }
        /* ========= DBFlyway ========== */
        final List<DBFlyway> flywayList = HPI.findMany(DBFlyway.class);
        final String dbType = ENV.of().get(EnvironmentVariable.DB_TYPE);
        for (final DBFlyway flyway : flywayList) {
            final List<String> pluginLocs = flyway.waitFlyway(dbType);
            if (Objects.nonNull(pluginLocs) || !pluginLocs.isEmpty()) {
                log.info("[ PLUG ] ( Flyway ) 检测到 DBFlyway 插件: {} / size = {}", flyway.getClass().getName(), pluginLocs.size());
                locations.addAll(pluginLocs);
            }
        }
        if (!locations.isEmpty()) {
            final StringBuilder content = new StringBuilder();
            content.append("[ PLUG ] Flyway 配置 Locations 列表：\n");
            for (final String loc : locations) {
                content.append("\t-- ").append(loc).append("\n");
            }
            log.info(content.toString());
            fc.locations(locations.toArray(new String[0]));
        }
        final String encoding = config.options(FlywayKeys.ENCODING);
        if (encoding != null && !encoding.isBlank()) {
            fc.encoding(encoding);
        }

        /* ========= 命名/规则 ========= */
        final String sqlPrefix = config.options(FlywayKeys.SQL_MIGRATION_PREFIX);
        if (sqlPrefix != null) {
            fc.sqlMigrationPrefix(sqlPrefix);
        }
        final String repeatPrefix = config.options(FlywayKeys.REPEATABLE_SQL_MIGRATION_PREFIX);
        if (repeatPrefix != null) {
            fc.repeatableSqlMigrationPrefix(repeatPrefix);
        }
        final String separator = config.options(FlywayKeys.SQL_MIGRATION_SEPARATOR);
        if (separator != null) {
            fc.sqlMigrationSeparator(separator);
        }
        final List<String> suffixes = asStringList(config.options(FlywayKeys.SQL_MIGRATION_SUFFIXES));
        if (!suffixes.isEmpty()) {
            fc.sqlMigrationSuffixes(suffixes.toArray(new String[0]));
        }

        /* ========= 验证/基线 ========= */
        final Boolean validateOnMigrate = config.options(FlywayKeys.VALIDATE_ON_MIGRATE);
        if (validateOnMigrate != null) {
            fc.validateOnMigrate(validateOnMigrate);
        }
        final Boolean validateNaming = config.options(FlywayKeys.VALIDATE_MIGRATION_NAMING);
        if (validateNaming != null) {
            fc.validateMigrationNaming(validateNaming);
        }
        final Boolean baselineOnMigrate = config.options(FlywayKeys.BASELINE_ON_MIGRATE);
        if (baselineOnMigrate != null) {
            fc.baselineOnMigrate(baselineOnMigrate);
        }
        final Object baselineVerRaw = config.options(FlywayKeys.BASELINE_VERSION);
        if (baselineVerRaw != null) {
            final MigrationVersion mv = toVersion(baselineVerRaw);
            if (mv != null) {
                fc.baselineVersion(mv);
            }
        }
        final String baselineDesc = config.options(FlywayKeys.BASELINE_DESCRIPTION);
        if (baselineDesc != null) {
            fc.baselineDescription(baselineDesc);
        }

        /* ========= 清理/安全 ========= */
        final Boolean cleanDisabled = config.options(FlywayKeys.CLEAN_DISABLED);
        if (cleanDisabled != null) {
            fc.cleanDisabled(cleanDisabled);
        }
        final Boolean cleanOnValidationError = config.options(FlywayKeys.CLEAN_ON_VALIDATION_ERROR);
        if (cleanOnValidationError != null) {
            fc.cleanOnValidationError(cleanOnValidationError);
        }

        /* ========= 执行策略 ========= */
        final Boolean group = config.options(FlywayKeys.GROUP);
        if (group != null) {
            fc.group(group);
        }
        final Boolean outOfOrder = config.options(FlywayKeys.OUT_OF_ORDER);
        if (outOfOrder != null) {
            fc.outOfOrder(outOfOrder);
        }
        final Boolean mixed = config.options(FlywayKeys.MIXED);
        if (mixed != null) {
            fc.mixed(mixed);
        }

        // Flyway 11：ignoreMissing/ignoreFuture → ignoreMigrationPatterns("*:missing", "*:future")
        final Boolean ignoreMissing = config.options(FlywayKeys.IGNORE_MISSING_MIGRATIONS);
        final Boolean ignoreFuture = config.options(FlywayKeys.IGNORE_FUTURE_MIGRATIONS);
        final List<String> ignorePatterns = new ArrayList<>(2);
        if (Boolean.TRUE.equals(ignoreMissing)) {
            ignorePatterns.add("*:missing");
        }
        if (Boolean.TRUE.equals(ignoreFuture)) {
            ignorePatterns.add("*:future");
        }
        if (!ignorePatterns.isEmpty()) {
            fc.ignoreMigrationPatterns(ignorePatterns.toArray(new String[0]));
        }

        final Boolean failMissingLoc = config.options(FlywayKeys.FAIL_ON_MISSING_LOCATIONS);
        if (failMissingLoc != null) {
            fc.failOnMissingLocations(failMissingLoc);
        }
        final Boolean createSchemas = config.options(FlywayKeys.CREATE_SCHEMAS);
        if (createSchemas != null) {
            fc.createSchemas(createSchemas);
        }

        final Object targetRaw = config.options(FlywayKeys.TARGET);
        if (targetRaw != null) {
            final MigrationVersion target = "latest".equalsIgnoreCase(String.valueOf(targetRaw))
                ? MigrationVersion.LATEST
                : toVersion(targetRaw);
            if (target != null) {
                fc.target(target);
            }
        }

        /* ========= 历史表 & Schema ========= */
        final String table = config.options(FlywayKeys.TABLE);
        if (table != null) {
            fc.table(table);
        }
        final String defaultSchema = config.options(FlywayKeys.DEFAULT_SCHEMA);
        if (defaultSchema != null) {
            fc.defaultSchema(defaultSchema);
        }
        final List<String> schemas = asStringList(config.options(FlywayKeys.SCHEMAS));
        if (!schemas.isEmpty()) {
            fc.schemas(schemas.toArray(new String[0]));
        }

        /* ========= 占位符 ========= */
        final Boolean placeholderReplacement = config.options(FlywayKeys.PLACEHOLDER_REPLACEMENT);
        if (placeholderReplacement != null) {
            fc.placeholderReplacement(placeholderReplacement);
        }
        final String placeholderPrefix = config.options(FlywayKeys.PLACEHOLDER_PREFIX);
        if (placeholderPrefix != null) {
            fc.placeholderPrefix(placeholderPrefix);
        }
        final String placeholderSuffix = config.options(FlywayKeys.PLACEHOLDER_SUFFIX);
        if (placeholderSuffix != null) {
            fc.placeholderSuffix(placeholderSuffix);
        }
        final Map<String, String> placeholders = asStringMap(config.options(FlywayKeys.PLACEHOLDERS));
        if (!placeholders.isEmpty()) {
            fc.placeholders(placeholders);
        }

        /* ========= 重试/锁 ========= */
        final Integer connectRetries = config.options(FlywayKeys.CONNECT_RETRIES);
        if (connectRetries != null) {
            fc.connectRetries(connectRetries);
        }
        final Integer lockRetryCount = config.options(FlywayKeys.LOCK_RETRY_COUNT);
        if (lockRetryCount != null) {
            fc.lockRetryCount(lockRetryCount);
        }

        /* ========= 可选（Teams 功能：Cherry Pick） ========= */
        final List<String> cherryPick = asStringList(config.options(FlywayKeys.CHERRY_PICK));
        if (!cherryPick.isEmpty()) {
            try {
                // 通过反射调用：FluentConfiguration cherryPick(String... versions)
                final Method m = fc.getClass().getMethod("cherryPick", String[].class);
                m.invoke(fc, new Object[]{cherryPick.toArray(new String[0])});
            } catch (final NoSuchMethodException e) {
                // 核心版无该方法：安全忽略（不影响编译与运行）
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException("[ PLUG ] 特殊配置异常：", e);
            }
        }

        // detect-encoding（如版本提供该开关）
        final Boolean detectEncoding = config.options(FlywayKeys.DETECT_ENCODING);
        if (detectEncoding != null) {
            fc.detectEncoding(detectEncoding);
        }

        return fc;
    }

    /* =========================================
     * 极简辅助：把任意 options 值转成 List/Map
     * ========================================= */

    private static List<String> asStringList(final Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof final List<?> src) {
            final List<String> out = new ArrayList<>(src.size());
            for (final Object o : src) {
                if (o != null) {
                    out.add(String.valueOf(o).trim());
                }
            }
            return out;
        }
        if (raw.getClass().isArray()) {
            final int len = Array.getLength(raw);
            final List<String> out = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                final Object o = Array.get(raw, i);
                if (o != null) {
                    out.add(String.valueOf(o).trim());
                }
            }
            return out;
        }
        final String s = String.valueOf(raw).trim();
        return s.isEmpty() ? List.of() : List.of(s);
    }

    private static Map<String, String> asStringMap(final Object raw) {
        final Map<String, String> outMap = new LinkedHashMap<>();
        switch (raw) {
            case null -> {
                return Map.of();
            }
            case final JsonObject srcJ -> {
                final Map<String, String> out = new LinkedHashMap<>();
                srcJ.forEach(entry -> {
                    final String k = entry.getKey();
                    final Object v = entry.getValue();
                    if (v != null) {
                        out.put(k, String.valueOf(v));
                    }
                });
                outMap.putAll(out);
            }
            case final Map<?, ?> src -> {
                final Map<String, String> out = new LinkedHashMap<>(src.size());
                src.forEach((k, v) -> {
                    if (k != null && v != null) {
                        out.put(String.valueOf(k), String.valueOf(v));
                    }
                });
                outMap.putAll(out);
            }
            default -> {
            }
        }
        // 环境变量的执行和处理
        final ENV env = ENV.of();
        Set.of(
            "DEV_MOBILE",
            "DEV_EMAIL",
            "DEV_ALIPAY"
        ).forEach(varName -> {
            final String devMobile = env.get(varName);
            outMap.put(varName, devMobile);
        });
        return outMap;
    }

    private static MigrationVersion toVersion(final Object raw) {
        return switch (raw) {
            case null -> null;
            case final MigrationVersion migrationVersion -> migrationVersion;
            case final Number number -> MigrationVersion.fromVersion(String.valueOf(number));
            default -> MigrationVersion.fromVersion(String.valueOf(raw));
        };
    }
}
