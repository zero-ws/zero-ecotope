package io.zerows.extension.runtime.tpl.util;

import io.zerows.epoch.common.uca.log.Log;
import io.zerows.epoch.common.uca.log.LogModule;

/*
 * Tool class available in current service only
 */
public class Tl {

    public interface LOG {
        String MODULE = "Πρότυπο";

        LogModule Qr = Log.modulat(MODULE).extension("Qr");
        LogModule Tpl = Log.modulat(MODULE).extension("Tpl");
    }
}
