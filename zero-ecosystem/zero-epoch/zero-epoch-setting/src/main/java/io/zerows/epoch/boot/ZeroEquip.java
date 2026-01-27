package io.zerows.epoch.boot;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.configuration.ConfigMod;
import io.zerows.epoch.spec.InPre;
import io.zerows.spi.HPI;

import java.util.Objects;

public class ZeroEquip {
    private static final Cc<String, ConfigMod> CC_MOD = Cc.openThread();
    private static final Cc<String, io.zerows.epoch.configuration.ConfigLoad> CC_LOAD = Cc.openThread();

    public static io.zerows.epoch.configuration.ConfigLoad ofLocal() {
        return CC_LOAD.pick(ConfigLoadHFS::new, ConfigLoadHFS.class.getName());
    }

    public static io.zerows.epoch.configuration.ConfigLoad ofCloud(final InPre pre) {
        Objects.requireNonNull(pre, "[ ZERO ] 云端连接入口配置不可为空！");
        final String cacheKey = ConfigLoadCloud.class.getName() + "@" + pre.hashCode();
        return CC_LOAD.pick(() -> new ConfigLoadCloud(pre), cacheKey);
    }

    public static ConfigMod of() {
        return of(null);
    }

    public static ConfigMod of(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return CC_MOD.pick(() -> HPI.findOneOf(ConfigMod.class), "DEFAULT");
        }
        return CC_MOD.pick(() -> SourceReflect.instance(clazz), clazz.getName());
    }
}
