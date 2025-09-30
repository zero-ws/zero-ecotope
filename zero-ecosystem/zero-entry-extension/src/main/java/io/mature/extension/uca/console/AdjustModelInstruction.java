package io.mature.extension.uca.console;

import io.mature.extension.migration.MigrateStep;
import io.mature.extension.migration.restore.MetaLimit;
import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.specification.access.app.HArk;
import io.zerows.unity.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AdjustModelInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final String appName = this.inString(args, "a");
        return this.partyB(appName).compose(okB -> {
            final HArk ark = okB.configArk();
            /* 修正模型数据专用 */
            final MigrateStep step = new MetaLimit(this.environment);
            return step.bind(ark).procAsync(new JsonObject()).compose(config -> {
                Sl.output("模型数据修正完成！");
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });
    }
}
