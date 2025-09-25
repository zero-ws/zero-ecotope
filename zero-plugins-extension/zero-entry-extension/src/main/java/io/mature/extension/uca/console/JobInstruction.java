package io.mature.extension.uca.console;

import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.zerows.unity.Ux;
import io.zerows.core.database.cp.zdk.DataPool;
import io.zerows.extension.runtime.skeleton.boot.supply.DataImport;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import org.jooq.DSLContext;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class JobInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        /*
         * 删除原始的Job
         */
        final DataPool pool = DataPool.create();
        final DSLContext context = pool.getExecutor();
        context.execute("DELETE FROM I_SERVICE WHERE `KEY` IN (SELECT SERVICE_ID FROM I_JOB)");
        context.execute("DELETE FROM I_JOB");
        /*
         * 重新导入
         */
        final String prefix = this.inString(args, "p");
        final DataImport importer = DataImport.of();
        return importer.loadAsync("init/oob/", prefix).compose(done -> {
            Sl.output("您的任务更新完成，更新结果：{0}", done);
            return Ux.future(done ? EmCommand.TermStatus.SUCCESS : EmCommand.TermStatus.FAILURE);
        });
    }
}
