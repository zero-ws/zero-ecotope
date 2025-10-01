package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.runtime.skeleton.boot.supply.DataImport;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class LoadInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final Boolean isOob = this.inBoolean(args, "o");
        // Fix Issue
        final boolean oob;
        if (Objects.isNull(isOob)) {
            oob = false;
        } else {
            oob = isOob;
        }
        return this.partyA().compose(ok -> {
            final DataImport importer = DataImport.of();
            if (oob) {
                return importer.landAsync("init/oob/");
            } else {
                return importer.loadAsync("init/oob");
            }
        }).compose(done -> {
            Sl.output("您的元数据仓库已重置初始化完成！重置结果：{0}", done);
            return Ux.future(done ? EmCommand.TermStatus.SUCCESS : EmCommand.TermStatus.FAILURE);
        });
    }
}
