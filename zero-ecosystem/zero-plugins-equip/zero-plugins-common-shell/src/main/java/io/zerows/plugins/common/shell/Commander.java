package io.zerows.plugins.common.shell;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.ams.constant.em.Environment;
import io.zerows.plugins.common.shell.atom.CommandAtom;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Commander {

    Commander bind(Environment environment);

    Commander bind(CommandAtom options);

    Commander bind(Vertx vertx);

    EmCommand.TermStatus execute(CommandInput args);

    Future<EmCommand.TermStatus> executeAsync(CommandInput args);
}
