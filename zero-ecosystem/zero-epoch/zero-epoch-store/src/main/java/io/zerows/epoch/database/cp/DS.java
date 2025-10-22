package io.zerows.epoch.database.cp;

import io.vertx.core.MultiMap;

/*
 * Data Source switching channel
 * For dynamic data source capture here instead of
 * Only one
 */
@Deprecated
public interface DS {
    /*
     * Sync method to findRunning `Data Source`
     */
    DataPool switchDs(MultiMap headers);

    DataPool switchDs(String sigma);
}
