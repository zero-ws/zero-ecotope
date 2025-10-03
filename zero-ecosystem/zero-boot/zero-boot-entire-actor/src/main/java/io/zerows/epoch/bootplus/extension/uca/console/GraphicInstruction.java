package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.epoch.bootplus.extension.uca.graphic.Plotter;
import io.zerows.epoch.bootplus.extension.uca.graphic.TopologyPlotter;
import io.zerows.program.Ux;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.specification.access.app.HArk;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class GraphicInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final String nodeId = this.inString(args, "n");
        final String edgeId = this.inString(args, "e");
        final String appName = this.inString(args, "a");
        /*
         * 绘图仪
         */
        return this.partyB(appName).compose(okB -> {
            final HArk app = okB.configArk();
            final Plotter plotter = new TopologyPlotter();
            plotter.bind(app);
            return plotter.drawAsync(nodeId, edgeId, this.ignores()).compose(finished -> {
                Sl.output("图引擎初始化完成！");
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });

    }
}
