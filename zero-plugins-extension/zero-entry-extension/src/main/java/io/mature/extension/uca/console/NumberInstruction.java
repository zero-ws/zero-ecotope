package io.mature.extension.uca.console;

import io.zerows.specification.access.app.HApp;
import io.mature.extension.migration.MigrateStep;
import io.mature.extension.migration.StepNumeric;
import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.zerows.unity.Ux;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;

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
