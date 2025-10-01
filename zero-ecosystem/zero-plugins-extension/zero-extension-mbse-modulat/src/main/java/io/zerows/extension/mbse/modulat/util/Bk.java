package io.zerows.extension.mbse.modulat.util;

import io.zerows.epoch.common.log.Log;
import io.zerows.epoch.common.log.LogModule;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class Bk {
    public interface LOG {
        String MODULE = "Πρότυπο";

        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Spi = Log.modulat(MODULE).extension("Service Loader");
    }
}
