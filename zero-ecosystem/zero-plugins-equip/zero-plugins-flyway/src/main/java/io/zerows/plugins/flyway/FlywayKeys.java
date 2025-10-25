package io.zerows.plugins.flyway;

final class FlywayKeys {
    private FlywayKeys() {
    }

    // 开关 & 连接
    static final String DISABLED = "disabled";
    static final String DATABASE = "database";

    static final String URL = "url";
    static final String USER = "user";
    static final String PASSWORD = "password";
    static final String DRIVER_CLASS_NAME = "driver-class-name";

    // 位置 & 编码
    static final String LOCATIONS = "locations";                 // List<String> | String
    static final String ENCODING = "encoding";                   // String

    // 命名/规则
    static final String SQL_MIGRATION_PREFIX = "sql-migration-prefix";
    static final String REPEATABLE_SQL_MIGRATION_PREFIX = "repeatable-sql-migration-prefix";
    static final String SQL_MIGRATION_SEPARATOR = "sql-migration-separator";
    static final String SQL_MIGRATION_SUFFIXES = "sql-migration-suffixes"; // List<String> | String

    // 验证/基线
    static final String VALIDATE_ON_MIGRATE = "validate-on-migrate";
    static final String VALIDATE_MIGRATION_NAMING = "validate-migration-naming";
    static final String BASELINE_ON_MIGRATE = "baseline-on-migrate";
    static final String BASELINE_VERSION = "baseline-version";   // String | Number
    static final String BASELINE_DESCRIPTION = "baseline-description";

    // 安全
    static final String CLEAN_DISABLED = "clean-disabled";
    static final String CLEAN_ON_VALIDATION_ERROR = "clean-on-validation-error";

    // 执行策略
    static final String GROUP = "group";
    static final String OUT_OF_ORDER = "out-of-order";
    static final String MIXED = "mixed";
    static final String IGNORE_MISSING_MIGRATIONS = "ignore-missing-migrations";
    static final String IGNORE_FUTURE_MIGRATIONS = "ignore-future-migrations";
    static final String FAIL_ON_MISSING_LOCATIONS = "fail-on-missing-locations";
    static final String CREATE_SCHEMAS = "create-schemas";
    static final String TARGET = "target"; // "latest" or version string

    // 历史表 & Schema
    static final String TABLE = "table";
    static final String DEFAULT_SCHEMA = "default-schema";
    static final String SCHEMAS = "schemas"; // List<String> | String

    // 占位符
    static final String PLACEHOLDER_REPLACEMENT = "placeholder-replacement";
    static final String PLACEHOLDER_PREFIX = "placeholder-prefix";
    static final String PLACEHOLDER_SUFFIX = "placeholder-suffix";
    static final String PLACEHOLDERS = "placeholders"; // Map<String, String>

    // 重试/锁
    static final String CONNECT_RETRIES = "connect-retries";
    static final String LOCK_RETRY_COUNT = "lock-retry-count";

    // 可选
    static final String CHERRY_PICK = "cherry-pick";             // List<String> | String
    static final String DETECT_ENCODING = "detect-encoding";     // boolean
}
