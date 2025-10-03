package io.zerows.extension.runtime.tpl.util;

import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;

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
