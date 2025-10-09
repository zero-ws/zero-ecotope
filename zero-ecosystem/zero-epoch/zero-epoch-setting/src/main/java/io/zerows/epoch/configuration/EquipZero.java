package io.zerows.epoch.configuration;

import io.r2mo.function.Fn;
import io.zerows.epoch.basicore.YmBoot;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmVertx;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.platform.enums.EmBoot;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 将 {@link YmConfiguration} 转换成 {@link HSetting} 的核心实现逻辑，有必要会更改 {@link HSetting} 的接口设计，以
 * 保证为上层提供整个配置服务的能力，代码执行到此处已完成了 Nacos 对接，所以此处不再考虑配置本身的来源问题。
 *
 * @author lang : 2025-10-08
 */
class EquipZero implements Equip {
    @Override
    public HSetting initialize(final YmConfiguration configuration) {
        final ZeroSetting setting = ZeroSetting.of();
        // ID 绑定，对应 vertx.application.name 的值，无分配时使用随机数，推荐使用固定值
        setting.id(configuration.id());

        // 添加 launcher 启动周期的 setting
        this.initialize(setting, configuration.getBoot());

        // 添加主容器配置
        this.initialize(setting, configuration.getVertx());
        return setting;
    }

    private void initialize(final ZeroSetting setting, final YmVertx vertx) {
        
    }

    private void initialize(final ZeroSetting setting, final YmBoot boot) {
        Objects.requireNonNull(boot, "[ ZERO ] 启动配置不能为空！");
        final ZeroConfig configLauncher = new ZeroConfig(boot.getLauncher());
        setting.launcher(configLauncher);

        /* 启动过程中的生命周期组件配置 */
        this.initialize(setting, EmBoot.LifeCycle.ON, boot::getOn);
        this.initialize(setting, EmBoot.LifeCycle.OFF, boot::getOff);
        this.initialize(setting, EmBoot.LifeCycle.PRE, boot::getPre);
        this.initialize(setting, EmBoot.LifeCycle.RUN, boot::getRun);
    }

    private void initialize(final ZeroSetting setting, final EmBoot.LifeCycle life,
                            final Supplier<MMComponent> componentFn) {
        final MMComponent component = componentFn.get();
        Fn.jvmAt(Objects.nonNull(component), () -> {
            final ZeroConfig configOn = new ZeroConfig(component);
            setting.boot(life, configOn);
        });
    }
}
