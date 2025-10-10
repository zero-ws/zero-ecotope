package io.zerows.epoch.boot;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.configuration.HSetting;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 当前类只负责组件，内部保存 setting 的引用，用来构造核心组件
 *
 * @author lang : 2023-05-31
 */
@Slf4j
class ZeroBoot implements HBoot {
    private Class<?> mainClass;
    private final HLauncher<?> launcher;
    private final HSetting setting;
    private final EmApp.Type type;
    private final Cc<EmApp.LifeCycle, Object> CC_BOOT = Cc.open();

    private ZeroBoot(final HSetting setting) {
        this.type = EmApp.Type.APPLICATION;

        this.launcher = this.createLauncher(setting.launcher());

        log.info("[ ZERO ] 选择启动器：{}", this.launcher.getClass().getName());

        this.setting = setting;
    }

    private HLauncher<?> createLauncher(final HConfig config) {
        HLauncher<?> launcher = null;
        // 初步查找：自定义的特殊 Launcher
        if (Objects.nonNull(config)) {
            final Class<?> launcherCls = config.executor();
            if (Objects.nonNull(launcherCls)) {
                launcher = SourceReflect.instance(launcherCls);
            }
        }
        // 二次查找
        if (Objects.isNull(launcher)) {
            // 基于 SPI 的优先级模式下的 Launcher
            launcher = HPI.findOneOf(HLauncher.class);
        }
        Objects.requireNonNull(launcher, "[ ZERO ] 系统必须找到一个启动器 HLauncher");
        return launcher;
    }

    static ZeroBoot of(final HSetting setting) {
        return new ZeroBoot(setting);
    }

    @Override
    public EmApp.Type app() {
        return this.type;
    }

    @Override
    public Class<?> mainClass() {
        return this.mainClass;
    }

    public ZeroBoot mainClass(final Class<?> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> HLauncher<C> launcher() {
        return (HLauncher<C>) this.launcher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> HLauncher.Pre<C> withPre() {
        return (HLauncher.Pre<C>) this.CC_BOOT.pick(
            () -> this.createLifeCycle(EmApp.LifeCycle.PRE), EmApp.LifeCycle.PRE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends HConfig> HConfig.HOn<T> whenOn() {
        return (HConfig.HOn<T>) this.CC_BOOT.pick(
            () -> this.createLifeCycle(EmApp.LifeCycle.ON), EmApp.LifeCycle.ON);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends HConfig> HConfig.HRun<T> whenRun() {
        return (HConfig.HRun<T>) this.CC_BOOT.pick(
            () -> this.createLifeCycle(EmApp.LifeCycle.RUN), EmApp.LifeCycle.RUN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends HConfig> HConfig.HOff<T> whenOff() {
        return (HConfig.HOff<T>) this.CC_BOOT.pick(
            () -> this.createLifeCycle(EmApp.LifeCycle.OFF), EmApp.LifeCycle.OFF);
    }

    @SuppressWarnings("all")
    private <R> R createLifeCycle(final EmApp.LifeCycle lifeCycle) {
        final HConfig config = this.setting.boot(lifeCycle);
        if (Objects.isNull(config)) {
            return null;
        }
        final Class<?> componentCls = config.executor();
        if (Objects.isNull(componentCls)) {
            return null;
        }
        final R ref = SourceReflect.instance(componentCls);
        if (ref instanceof final HConfig.HOn on) {
            on.configure(config.options());
            log.info("[ ZERO ] 配置组件：On = {} / Configuration = {}", on.getClass(), componentCls);
            log.info("[ ZERO ] 配置参数：\n{}", config.options().encodePrettily());
        }
        return ref;
    }
}
