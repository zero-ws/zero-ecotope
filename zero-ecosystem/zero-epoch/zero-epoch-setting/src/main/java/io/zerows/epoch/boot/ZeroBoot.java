package io.zerows.epoch.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmBoot;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HLauncher;

/**
 * @author lang : 2023-05-31
 */
public class ZeroBoot implements HBoot {
    private Class<?> launcherCls;
    private Class<?> mainClass;
    private String[] arguments;

    private EmApp.Type type;

    private ZeroBoot(final YmBoot bootConfiguration) {
        // this.launcherCls = UtBase.valueC(bootJ, VertxYml.boot.launcher);
        // this.energy = ZeroEnergy.of(bootJ);
    }

    public static HBoot of(final JsonObject bootJ) {
        return new ZeroBoot(null);
    }

    @Override
    public EmApp.Type app() {
        return null;
    }

    @Override
    public String[] inArgs() {
        return new String[0];
    }

    @Override
    public Class<?> inMain() {
        return null;
    }

    @Override
    public <C> HLauncher<C> launcher() {
        return null;
    }

    @Override
    public <C> HLauncher.Pre<C> withPre() {
        return null;
    }

    @Override
    public <T extends HConfig> HConfig.HOn<T> whenOn() {
        return null;
    }

    @Override
    public <T extends HConfig> HConfig.HRun<T> whenRun() {
        return null;
    }

    @Override
    public <T extends HConfig> HConfig.HOff<T> whenOff() {
        return null;
    }
}
