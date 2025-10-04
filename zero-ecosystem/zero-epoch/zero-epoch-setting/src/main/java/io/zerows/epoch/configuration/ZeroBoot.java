package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VBoot;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.support.base.UtBase;

/**
 * @author lang : 2023-05-31
 */
public class ZeroBoot implements HBoot {
    private final HEnergy energy;
    private final Class<?> launcherCls;
    private Class<?> mainClass;
    private String[] arguments;

    private EmApp.Type type;

    private ZeroBoot(final JsonObject bootJ) {
        this.launcherCls = UtBase.valueC(bootJ, VBoot.LAUNCHER);
        this.energy = ZeroEnergy.of(bootJ);
    }

    public static HBoot of(final JsonObject bootJ) {
        return new ZeroBoot(bootJ);
    }

    @Override
    public HBoot bind(final Class<?> mainClass, final String... arguments) {
        this.mainClass = mainClass;
        this.arguments = arguments;
        return this;
    }

    @Override
    public EmApp.Type app() {
        return this.type;
    }

    @Override
    public HBoot app(final EmApp.Type app) {
        this.type = app;
        return this;
    }

    @Override
    public Class<?> target() {
        return this.mainClass;
    }

    @Override
    public Class<?> launcher() {
        return this.launcherCls;
    }

    @Override
    public HEnergy energy() {
        return this.energy;
    }

    @Override
    public String[] args() {
        return this.arguments;
    }
}
