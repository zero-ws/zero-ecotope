package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.epoch.corpus.Ux;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.plugins.store.neo4j.Neo4jClient;
import io.zerows.plugins.store.neo4j.Neo4jInfix;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ResetNeo4jInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        return this.partyA().compose(ok -> {
            final String group = this.inString(args, "g");
            /* 默认分组：__VERTX_ZERO__ */
            final Neo4jClient client = Neo4jInfix.getClient().connect(group);

            return client.graphicReset().compose(finished -> {
                Sl.output("Neo4j图库重置完成！");
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        });
    }
}
