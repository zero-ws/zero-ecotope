package io.mature.extension.uca.console;

import io.mature.extension.migration.MigrateStep;
import io.mature.extension.migration.backup.BackupHistory;
import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.specification.access.app.HApp;
import io.zerows.unity.Ux;

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
