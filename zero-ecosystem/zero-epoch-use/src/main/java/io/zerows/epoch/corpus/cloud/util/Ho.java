package io.zerows.epoch.corpus.cloud.util;

import io.zerows.epoch.common.log.Log;
import io.zerows.epoch.common.log.LogModule;

/**
 * @author lang : 2023/4/26
 */
public final class Ho {
    public interface LOG {
        String MODULE = "Δέκα δισεκατομμύρια";

        LogModule Aeon = Log.modulat(MODULE).cloud("Aeon");
        LogModule Fs = Log.modulat(MODULE).cloud("Fs");
        LogModule Env = Log.modulat(MODULE).cloud("Env");
        LogModule K8S = Log.modulat(MODULE).cloud("K8s");
    }
}
