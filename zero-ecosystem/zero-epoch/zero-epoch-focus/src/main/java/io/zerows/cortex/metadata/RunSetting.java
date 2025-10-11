package io.zerows.cortex.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;

/**
 * @author lang : 2025-10-11
 */ // ------------------- 功能启用的核心函数，拒绝 HSetting 的直接启用调用
class RunSetting {
    private final HSetting setting;

    RunSetting(final NodeVertx nodeVertx) {
        this.setting = nodeVertx.networkRef().setting();
        Objects.requireNonNull(this.setting, "[ ZERO ] HSetting 对象丢失！");
    }

    boolean session() {
        return Objects.nonNull(this.setting.infix(EmApp.Native.SESSION));
    }

    CorsOptions optionsCors() {
        final HConfig config = this.setting.infix(EmApp.Native.CORS);
        return Objects.requireNonNull(config).ref();
    }

    JsonObject optionsSession() {
        final HConfig config = this.setting.infix(EmApp.Native.SESSION);
        return Objects.requireNonNull(config).options();
    }
}
