package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.InPre;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.access.app.HApp;

import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
public interface ConfigLoad {

    Cc<String, ConfigLoad> CC_SKELETON = Cc.openThread();

    static ConfigLoad ofLocal() {
        return CC_SKELETON.pick(ConfigLoadHFS::new, ConfigLoadHFS.class.getName());
    }

    static ConfigLoad ofCloud(final InPre pre) {
        Objects.requireNonNull(pre, "[ ZERO ] 云端连接入口配置不可为空！");
        final String cacheKey = ConfigLoadCloud.class.getName() + "@" + pre.hashCode();
        return CC_SKELETON.pick(() -> new ConfigLoadCloud(pre), cacheKey);
    }

    /**
     * 此处的 HApp 中只要包含
     * <pre>
     *     id       - 应用id
     *     tenant   - 租户id
     *     name     - 应用名称
     *     ns       - 应用名空间
     * </pre>
     *
     * @param app 应用信息
     *
     * @return 配置对象
     */
    YmConfiguration configure(HApp app);
}
