package io.zerows.epoch.bootplus.extension.migration.tookit;

import io.r2mo.base.dbe.Database;
import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.DBSActor;
import io.zerows.platform.enums.Environment;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import static io.zerows.epoch.bootplus.extension.refine.Ox.LOG;

@Slf4j
public class TableHugeRestore extends AbstractStatic {
    public TableHugeRestore(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        /*
         * 目录创建
         */
        final String targetFolder = this.ioBackup(config, "sql");
        this.tableEmpty(this.jooq.table());
        /*
         * 文件名
         */
        final String file = targetFolder + "/" + this.jooq.table() + ".sql";
        final boolean done = this.restoreTo(file);
        LOG.Shell.info(this.getClass(), "数据还原文件：{0}，执行结果：{1}",
            file, done);
        return Ux.future(config);
    }

    private boolean restoreTo(final String file) {
        final StringBuilder cmd = new StringBuilder();
        final Database database = DBSActor.ofDatabase();
        cmd.append("mysql")
            .append(" -h").append(database.getHostname())
            .append(" -u").append(database.getUsername())
            .append(" -p").append(database.getPasswordDecrypted())
            .append(" --default-character-set=utf8 ")
            .append(" ").append(database.getInstance());
        return Fn.jvmOr(() -> {
            final File fileObj = Ut.ioFile(file);
            final BasicFileAttributes fileAttributes = Files.readAttributes(fileObj.toPath(), BasicFileAttributes.class);
            if (fileObj.exists() && fileAttributes.isRegularFile()) {
                log.info("[ ZERO ] ( MGN ) 文件名：{}，执行命令：{}，文件长度：{} MB", file, cmd, fileAttributes.size());

                final Process process = new ProcessBuilder().command(cmd.toString()).start();
                /*
                 * 开始时间
                 */
                final long start = System.nanoTime();
                final OutputStream outputStream = process.getOutputStream();
                /* 直接写输出流 */
                Ut.ioOut(file, outputStream);
                final long end = System.nanoTime();
                /* 纳秒 -> 毫秒 */
                final long spend = (end - start) / 1000 / 1000;
                log.info("[ ZERO ] ( MGN ) 执行完成，耗时 {} ms！ Successfully", spend);
                return true;
            } else {
                log.warn("[ ZERO ] ( MGN ) 文件不存在！file = {}", file);
                return false;
            }
        });
    }
}
