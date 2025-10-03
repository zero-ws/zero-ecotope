package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.migration.Migrate;
import io.zerows.epoch.bootplus.extension.migration.MigrateService;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BackUpInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        return this.executeMigrate(args, (app, config) -> {
            final Migrate migrate = Ut.singleton(MigrateService.class);
            return migrate.bind(this.environment).bind(app).backupAsync(config).compose(result -> {
                Sl.output("基础系统备份完成！目录：{0}", config.getString("output"));
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });
    }
}
