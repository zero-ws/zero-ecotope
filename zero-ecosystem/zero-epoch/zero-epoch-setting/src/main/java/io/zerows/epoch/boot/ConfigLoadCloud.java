package io.zerows.epoch.boot;

import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmEntrance;

/**
 * @author lang : 2025-10-06
 */
class ConfigLoadCloud implements ConfigLoad {
    private final YmEntrance entrance;

    ConfigLoadCloud(final YmEntrance entrance) {
        this.entrance = entrance;
    }

    @Override
    public YmConfiguration configure(final String app) {
        return null;
    }
}
