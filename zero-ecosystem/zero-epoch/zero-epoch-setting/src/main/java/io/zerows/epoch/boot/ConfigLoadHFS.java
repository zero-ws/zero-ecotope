package io.zerows.epoch.boot;

import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.app.HApp;

/**
 * HFS 模式的加载有两种情况
 * <pre>
 *     1. 存在 vertx.yml 配置
 *     2. 不存在 vertx.yml 配置（最小运行集）
 * </pre>
 *
 * @author lang : 2025-10-06
 */
class ConfigLoadHFS implements ConfigLoad {
    @Override
    public YmConfiguration configure(final HApp app) {

        return null;
    }
}
