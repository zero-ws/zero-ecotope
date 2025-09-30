package io.mature.extension.uca.console;

import io.mature.extension.refine.Ox;
import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.unity.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ResetEsInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final String appName = this.inString(args, "a");
        return this.runEach(appName, identifier -> this.partyA().compose(okA -> {
            final HArk ark = okA.configArk();
            final HApp app = ark.app();
            final DataAtom atom = Ao.toAtom(app.name(), identifier);
            return Ox.runEs(atom).compose(client -> Ux.future(Boolean.TRUE));
        })).compose(done -> {
            Sl.output("索引重建完成，重建模型数量：{0}", done.size());
            return Ux.future(EmCommand.TermStatus.SUCCESS);
        });
    }
}
