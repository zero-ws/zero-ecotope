package io.zerows.epoch.boot;

import io.zerows.epoch.spec.InPre;
import io.zerows.epoch.spec.InPreVertx;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;

import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
class ConfigLoadCloud implements io.zerows.epoch.configuration.ConfigLoad {
    private final InPre entrance;

    ConfigLoadCloud(final InPre entrance) {
        this.entrance = entrance;
        Objects.requireNonNull(entrance.getVertx(), "[ ZERO ] 云端连接配置 vertx 不可为空！");
    }

    @Override
    public YmConfiguration configure(final HApp app) {
        final InPreVertx vertx = this.entrance.getVertx();
        /*
         * 云端配置检查逻辑
         * vertx:
         *   config:
         *     import:
         *     - "???"
         *   application:
         *     name: "???"    # 已检查过，不再检查
         * ---
         * vertx:
         *   cloud:
         *     nacos:
         */
        return null;
    }
}
