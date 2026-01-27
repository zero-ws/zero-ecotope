package io.zerows.epoch.boot;

import io.zerows.epoch.configuration.ConfigLoadBase;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * HFS 模式的加载有两种情况
 * <pre>
 *     1. 存在 vertx.yml 配置
 *     2. 不存在 vertx.yml 配置（最小运行集）
 * </pre>
 *
 * @author lang : 2025-10-06
 */
@Slf4j
class ConfigLoadHFS extends ConfigLoadBase {
    private static final String FILE_VERTX = "vertx.yml";

    @Override
    public YmConfiguration configure(final HApp app) {
        final ConfigFs<YmConfiguration> fs = ZeroFs.of().inFs(FILE_VERTX, YmConfiguration.class);
        if (Objects.isNull(fs) || Objects.isNull(fs.refT())) {
            // 不存在 vertx.yml 的默认配置
            return YmConfiguration.createDefault();
        }
        final YmConfiguration inConfiguration = fs.refT();
        // 双模式处理
        return this.completeConfiguration(inConfiguration, fs.refJson(), app);
    }
}
