package io.zerows.component.log;

/**
 * @author lang : 2023/5/2
 */
public interface LogAs {
    String MODULE = "Προδιαγραφή μεταδεδομένων";

    LogModule Fs = Log.modulat(MODULE).cloud("Fs");
    LogModule Boot = Log.modulat(MODULE).extension("Boot");
    LogModule Spi = Log.modulat(MODULE).extension("Spi");
}
