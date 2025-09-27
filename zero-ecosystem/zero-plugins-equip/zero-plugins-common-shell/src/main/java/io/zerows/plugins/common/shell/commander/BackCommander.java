package io.zerows.plugins.common.shell.commander;

import io.zerows.plugins.common.shell.AbstractCommander;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BackCommander extends AbstractCommander {
    @Override
    public EmCommand.TermStatus execute(final CommandInput args) {
        Sl.goodbye(this.atom);
        return EmCommand.TermStatus.EXIT;
    }
}
