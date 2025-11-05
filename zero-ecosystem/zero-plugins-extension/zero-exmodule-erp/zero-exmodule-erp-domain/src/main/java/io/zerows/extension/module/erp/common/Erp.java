package io.zerows.extension.module.erp.common;

import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;

/*
 *
 */
public class Erp {

    public interface LOG {
        String MODULE = "Επιχείρηση";

        LogModule Worker = Log.modulat(MODULE).extension("Worker");
    }
}
