package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.bootplus.extension.migration.backup.BackupHistory;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HistoryInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        return this.executeMigrate(args, (ark, config) -> {
            final MigrateStep step = new BackupHistory(this.environment);
            return step.bind(ark).procAsync(config).compose(nil -> {
                final HApp app = ark.app();
                Sl.output("历史文件备份完成，应用：{0}", app.name());
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });
    }
}
