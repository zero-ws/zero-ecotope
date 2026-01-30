package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.ConfigFs;
import io.zerows.epoch.configuration.ConfigLoadBase;
import io.zerows.epoch.configuration.ConfigProvider;
import io.zerows.epoch.spec.InPre;
import io.zerows.epoch.spec.InPreArgs;
import io.zerows.epoch.spec.InPreVertx;
import io.zerows.epoch.spec.YmCloud;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.epoch.spec.exception._41003Exception500ConfigMissing;
import io.zerows.epoch.spec.exception._41004Exception501ProviderNone;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
class ConfigLoadCloud extends ConfigLoadBase {
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
        final YmCloud cloud = vertx.getCloud();
        Fn.jvmKo(Objects.isNull(cloud) || cloud.isEmpty(), _41003Exception500ConfigMissing.class, "vertx.cloud");
        final String selected = vertx.getSelected();
        Fn.jvmKo(Ut.isNil(selected), _41003Exception500ConfigMissing.class, "vertx.selected");
        final JsonObject options = cloud.getItem(selected);
        Fn.jvmKo(Ut.isNil(options), _41003Exception500ConfigMissing.class, "vertx.cloud." + selected);


        // 构造参数，加载 CloudServer/nacos 配置提供器加载配置
        final InPreArgs args = new InPreArgs();
        args.configVertx(vertx.getConfig()).options(options);
        final ConfigProvider provider = ConfigProvider.of(selected);
        Fn.jvmKo(Objects.isNull(provider), _41004Exception501ProviderNone.class, selected);
        final ConfigFs<YmConfiguration> fs = provider.configure(args, app);
        // 双模式处理
        return this.completeConfiguration(fs.refT(), fs.refJson(), app);
    }
}
