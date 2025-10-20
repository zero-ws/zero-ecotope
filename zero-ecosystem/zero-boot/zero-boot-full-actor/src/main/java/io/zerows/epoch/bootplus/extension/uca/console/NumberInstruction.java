package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.bootplus.extension.migration.StepNumeric;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class NumberInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        return this.executeMigrate(args, (ark, config) -> {
            final MigrateStep step = new StepNumeric(this.environment);
            return step.bind(ark).procAsync(config).compose(result -> {
                final HApp app = ark.app();
                Sl.output("序号修复完成，应用：{0}", app.name());
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });
    }
}
