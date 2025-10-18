package io.zerows.epoch.bootplus.extension.migration.tookit;

import io.r2mo.base.dbe.Database;
import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.DBSActor;
import io.zerows.platform.enums.Environment;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableHugeBackup extends AbstractStatic {

    public TableHugeBackup(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        /*
         * 目录创建
         */
        final String targetFolder = this.ioBackup(config, "sql");
        /*
         * 文件名
         */
        final String file = targetFolder + "/" + this.jooq.table() + ".sql";
        final boolean done = this.backupTo(file, this.jooq.table());
        log.info("[ ZERO ] ( MGN ) 备份数据位置：{}，执行结果：{}", file, done);
        return Ux.future(config);
    }

    private boolean backupTo(final String file, final String tableNames) {
        /*
         * mysqldump --opt
         * --host=localhost
         * --databases backup
         * --tables log_sys sys_user
         * --user=root
         * --password=root
         * --result-file=E:\Sqldata\mock.sql
         * --default-character-set=utf8
         */
        final StringBuilder cmd = new StringBuilder();
        final Database database = DBSActor.ofDatabase();
        cmd.append("mysqldump").append(" --opt")
            .append(" --host=").append(database.getHostname())
            .append(" --databases ").append(database.getInstance())
            .append(" --tables ").append(tableNames)
            .append(" --user=").append(database.getUsername())
            .append(" --password=").append(database.getPasswordDecrypted())
            .append(" --result-file=").append(file)
            .append(" --skip-comments")
            .append(" --default-character-set=utf8 ");
        return Fn.jvmOr(() -> {
            log.info("[ ZERO ] ( MGN ) 执行数据备份命令：{}", cmd);
            final Process process = new ProcessBuilder().command(cmd.toString()).start();
            return process.waitFor() == 0;
        });
    }
}
