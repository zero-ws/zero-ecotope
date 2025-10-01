package io.zerows.epoch.common.shared.boot;

import io.r2mo.typed.exception.AbstractException;
import io.zerows.epoch.constant.VMessage;
import io.zerows.epoch.enums.EmBoot;
import io.zerows.epoch.support.UtBase;
import io.zerows.epoch.common.log.LogAs;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.boot.HMature;

import java.util.Objects;
import java.util.Optional;

/**
 * 此类作为新类，作为 {@link KLauncher} 的组合类使用，当 KLauncher 遇到了测试环境或特殊环境时，容器本身不是通过 {@link KLauncher}
 * 的模式启动，而是作为特定的环境启动，但这种场景下 {@link HConfig.HOn} 部分依旧要生效，这种情况下，使用
 * {@link KConfigurer} 替换掉 {@link KLauncher} 来完成冷启动流程。
 * <pre><code>
 *     简单说其核心流程如下：
 *     1）冷启动：执行 {@link KConfigurer}
 *     2）热启动：先执行 {@link KConfigurer} 再执行 {@link KLauncher}
 * </code></pre>
 * 和 {@link KLauncher} 还有一个区别在于冷启动的配置部分可以是多个，但热启动只有一个实例（全局单件）。
 *
 * @author lang : 2023-06-13
 */
public class KConfigurer<T> {

    private final HEnergy energy;

    private String[] arguments;

    private KConfigurer(final HEnergy energy) {
        this.energy = energy;
        this.arguments = new String[]{};
    }

    public static <T> KConfigurer<T> of(final HEnergy energy) {
        return new KConfigurer<>(energy);
    }

    public KConfigurer<T> bind(final String[] args) {
        this.arguments = args;
        LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT_ARGS, args.length, UtBase.fromJoin(args));
        return this;
    }

    // Pre 执行 ----------------------------------------

    public <CONFIG extends HConfig> void preExecute(final T started, final CONFIG configuration) {
        final Class<?> preCls = this.energy.component(EmBoot.LifeCycle.PRE);
        Optional.ofNullable(preCls).ifPresent(pClass -> {
            // 配置绑定
            configuration.pre(pClass);
            final HMature.HPre<T> pre = UtBase.singleton(pClass);
            pre.beforeStart(started, configuration.options());
        });
    }

    // On ----------------------------------------------
    public HConfig onConfig() {
        final Class<?> implOn = this.energy.component(EmBoot.LifeCycle.ON);
        if (Objects.isNull(implOn)) {
            // 未配置组件，直接跳过
            return null;
        }
        return this.energy.config(implOn);
    }

    /**
     * {@link HConfig.HOn} 启动周期核心配置组件，执行生命周期组件中的
     * <pre><code>
     *     boot:
     *        component:
     *           on:
     *           pre:
     *           off:
     *           run:
     * </code></pre>
     *
     * @return {@link HConfig.HOn}
     */
    @SuppressWarnings("all")
    public HConfig.HOn onComponent() {
        final Class<?> implOn = this.energy.component(EmBoot.LifeCycle.ON);
        if (Objects.isNull(implOn)) {
            // 未配置组件，直接跳过
            return null;
        }
        HConfig.HOn on = UtBase.singleton(implOn);
        if (Objects.isNull(on)) {
            // 组件初始化失败，直接跳过
            return null;
        }
        // 启动参数提取
        on = on.args(this.arguments);

        final HConfig configuration = this.energy.config(on.getClass());

        // 初始化，返回结果
        try {
            if (Objects.nonNull(configuration)) {
                on.configure(configuration);
                LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT, on.getClass(), configuration.getClass());
                LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT_CONFIG, configuration.options().encodePrettily());
            }
            return on;
        } catch (final AbstractException error) {
            error.printStackTrace();
            throw error;
        }
    }
}
