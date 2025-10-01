package io.zerows.extension.commerce.erp.util;

import io.zerows.epoch.common.uca.log.Log;
import io.zerows.epoch.common.uca.log.LogModule;

/*
 *
 */
public class Er {

    public interface LOG {
        String MODULE = "Επιχείρηση";

        LogModule Worker = Log.modulat(MODULE).extension("Worker");
    }
}
