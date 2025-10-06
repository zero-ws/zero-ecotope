package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmEntrance;

import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
public interface ConfigLoad {

    Cc<String, ConfigLoad> CC_SKELETON = Cc.openThread();

    static ConfigLoad ofLocal() {
        return CC_SKELETON.pick(ConfigLoadHFS::new, ConfigLoadHFS.class.getName());
    }

    static ConfigLoad ofCloud(final YmEntrance entrance) {
        Objects.requireNonNull(entrance, "[ ZERO ] 云端连接入口配置不可为空！");
        final String cacheKey = ConfigLoadCloud.class.getName() + "@" + entrance.hashCode();
        return CC_SKELETON.pick(() -> new ConfigLoadCloud(entrance), cacheKey);
    }

    YmConfiguration configure(String app);
}
