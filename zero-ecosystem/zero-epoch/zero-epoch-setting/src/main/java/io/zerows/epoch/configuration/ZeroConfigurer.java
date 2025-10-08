package io.zerows.epoch.configuration;

import io.r2mo.typed.exception.AbstractException;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.platform.enums.EmBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.support.Ut;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

/**
 * 此类作为新类，作为 {@link ZeroLauncher} 的组合类使用，当 KLauncher 遇到了测试环境或特殊环境时，容器本身不是通过 {@link ZeroLauncher}
 * 的模式启动，而是作为特定的环境启动，但这种场景下 {@link HConfig.HOn} 部分依旧要生效，这种情况下，使用
 * {@link ZeroConfigurer} 替换掉 {@link ZeroLauncher} 来完成冷启动流程。
 * <pre><code>
 *     简单说其核心流程如下：
 *     1）冷启动：执行 {@link ZeroConfigurer}
 *     2）热启动：先执行 {@link ZeroConfigurer} 再执行 {@see KLauncher}
 * </code></pre>
 * 和 {@link ZeroLauncher} 还有一个区别在于冷启动的配置部分可以是多个，但热启动只有一个实例（全局单件）。
 *
 * @author lang : 2023-06-13
 */
@Deprecated
@Slf4j
public class ZeroConfigurer<T> {

    private final HEnergy energy;

    private String[] arguments;

    private ZeroConfigurer(final HEnergy energy) {
        this.energy = energy;
        this.arguments = new String[]{};
    }

    public static <T> ZeroConfigurer<T> of(final HEnergy energy) {
        return new ZeroConfigurer<>(energy);
    }

    public ZeroConfigurer<T> bind(final String[] args) {
        this.arguments = args;
        log.info("参数信息 = {}, 参数长度 = {}", Ut.fromJoin(args), args.length);
        return this;
    }

    // Pre 执行 ----------------------------------------

    public <CONFIG extends HConfig> void preExecute(final T started, final CONFIG configuration) {
        final Class<?> preCls = this.energy.component(EmBoot.LifeCycle.PRE);
        Optional.ofNullable(preCls).ifPresent(pClass -> {
            // 配置绑定
            // configuration.pre(pClass);
            final HLauncher.Pre<T> pre = UtBase.singleton(pClass);
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
                log.info("[ ZERO ] 配置组件：On = {} / Configuration = {}", on.getClass(), configuration.getClass());
                log.info("[ ZERO ] 配置参数：\n{0}", configuration.options().encodePrettily());
            }
            return on;
        } catch (final AbstractException error) {
            error.printStackTrace();
            throw error;
        }
    }
}
