package io.zerows.extension.module.modulat.common;

import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;

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
