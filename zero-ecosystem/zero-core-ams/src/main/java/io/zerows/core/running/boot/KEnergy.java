package io.zerows.core.running.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.em.EmApp;
import io.zerows.ams.constant.em.EmBoot;
import io.zerows.ams.constant.spec.VBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.ams.util.HUt;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「能量」配置数据标准化结构
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KEnergy implements HEnergy {
    private final ConcurrentMap<EmBoot.LifeCycle, Class<?>> component = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, HConfig> config = new ConcurrentHashMap<>();
    private final ConcurrentMap<EmApp.Mode, Class<?>> connect = new ConcurrentHashMap<>();
    private Class<?> rad;

    private KEnergy() {
    }

    /**
     * 根据配置文件生成能量配置
     * <pre><code>
     * boot:
     *     launcher:
     *     component:
     *        on:
     *        off:
     *        run:
     *        pre:
     *     config:
     *     connect:
     *     rad:
     * </code></pre>
     *
     * @param config 配置文件
     *
     * @return {@link HEnergy} 能量配置
     */
    public static HEnergy of(final JsonObject config) {
        final KEnergy energy = new KEnergy();
        final JsonObject component = HUt.valueJObject(config, VBoot.COMPONENT);
        final JsonObject configJ = HUt.valueJObject(config, VBoot.CONFIG);
        /*
         * - pre, 针对 on / off / run 的特殊配置
         * - on
         * - off
         * - run
         */
        HUt.<String>itJObject(component).forEach(entry -> {
            final EmBoot.LifeCycle lifeCycle = EmBoot.LifeCycle.from(entry.getKey());
            final Class<?> clazz = HUt.clazz(entry.getValue());
            energy.bind(lifeCycle, clazz);
            /*
             *  configJ
             *  - on = String
             *  - on = {
             *       component:
             *       config:
             *  }
             */
            final Object configV = configJ.getValue(entry.getKey());
            if (configV instanceof String) {
                final Class<?> instanceCls = HUt.clazz((String) configV, null);
                if (Objects.nonNull(instanceCls)) {
                    final HConfig configRef = HUt.singleton(instanceCls);
                    energy.bind(clazz, configRef);
                }
            } else if (configV instanceof JsonObject) {
                JsonObject options = (JsonObject) configV;
                final Class<?> instanceCls = HUt.valueC(options, VBoot.COMPONENT, null);
                if (Objects.nonNull(instanceCls)) {
                    final HConfig configRef = HUt.singleton(instanceCls);
                    options = options.copy();
                    options.remove(VBoot.COMPONENT);
                    configRef.options(options);
                    energy.bind(clazz, configRef);
                }
            }
        });
        return energy;
    }

    @Override
    public HEnergy bind(final EmBoot.LifeCycle lifeCycle, final Class<?> clazz) {
        this.component.put(lifeCycle, clazz);
        return this;
    }

    @Override
    public HEnergy bind(final EmApp.Mode appMode, final Class<?> clazz) {
        this.connect.put(appMode, clazz);
        return this;
    }

    @Override
    public HEnergy bind(final Class<?> clazz, final HConfig reference) {
        this.config.put(clazz, reference);
        return this;
    }

    @Override
    public HEnergy rad(final Class<?> rad) {
        this.rad = rad;
        return this;
    }

    @Override
    public Class<?> component(final EmBoot.LifeCycle lifeCycle) {
        return this.component.get(lifeCycle);
    }

    @Override
    public Class<?> component(final EmApp.Mode mode) {
        return this.connect.get(mode);
    }

    @Override
    public HConfig config(final Class<?> clazz) {
        return this.config.get(clazz);
    }

    @Override
    public Class<?> rad() {
        return this.rad;
    }
}
