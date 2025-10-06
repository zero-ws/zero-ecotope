package io.zerows.epoch.boot;

import io.zerows.epoch.basicore.InPre;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.access.app.HApp;

/**
 * @author lang : 2025-10-06
 */
class ConfigLoadCloud implements ConfigLoad {
    private final InPre entrance;

    ConfigLoadCloud(final InPre entrance) {
        this.entrance = entrance;
    }

    @Override
    public YmConfiguration configure(final HApp app) {
        return null;
    }
}
