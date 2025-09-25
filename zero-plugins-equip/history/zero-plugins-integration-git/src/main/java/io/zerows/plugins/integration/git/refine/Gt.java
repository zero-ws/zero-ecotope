package io.zerows.plugins.integration.git.refine;

import io.zerows.core.uca.log.Log;
import io.zerows.core.uca.log.LogModule;

/**
 * @author lang : 2023/4/26
 */
public final class Gt {
    public interface LOG {
        String INFIX = "πηγή";

        LogModule REPO = Log.modulat(INFIX).infix("Repo");
        LogModule COMMAND = Log.modulat(INFIX).infix("Command");
    }
}
