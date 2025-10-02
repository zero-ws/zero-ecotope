package io.zerows.epoch.bootplus.extension.migration;

import io.zerows.epoch.bootplus.extension.migration.backup.BackupAll;
import io.zerows.epoch.bootplus.extension.migration.backup.DDLExecution;
import io.zerows.epoch.bootplus.extension.migration.backup.EnvAll;
import io.zerows.epoch.bootplus.extension.migration.backup.ReportAll;
import io.zerows.epoch.bootplus.extension.migration.restore.RestoreAll;
import io.zerows.epoch.bootplus.extension.migration.restore.RestorePrepare;
import io.zerows.epoch.enums.Environment;

public class Actor {

    public static MigrateStep environment(final Environment environment) {
        return new EnvAll(environment);
    }

    /*
     * Backup 专用
     */
    static MigrateStep report(final Environment environment) {
        return new ReportAll(environment);
    }

    static MigrateStep backup(final Environment environment) {
        return new BackupAll(environment);
    }

    static MigrateStep ddl(final Environment environment) {
        return new DDLExecution(environment);
    }

    /*
     * Restore 专用
     */
    static MigrateStep prepare(final Environment environment) {
        return new RestorePrepare(environment);
    }

    static MigrateStep restore(final Environment environment) {
        return new RestoreAll(environment);
    }
}
