package io.zerows.extension.skeleton.metadata.base;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Module 消费端基类，这个抽象类比较特殊，子类必须实现抽象方法用于应用级初始化，简单说
 * <pre>
 *     1. 如果继承此类意味着应用模式下的 App 环境和当前模块是强绑定关系，如果是强绑定关系，那么必须实现子类的初始化逻辑。
 *     2. 若只继承 {@link MDActorOfModule} 则不需要考虑此处的问题
 *     3. {@link HAmbient} 本质是一个多应用环境，内置 key = {@link HArk} 的应用映射
 * </pre>
 * 后期所有多应用级的扩展可以直接在 {@link HAmbient} 中完成，这样可以保证模块的多应用能力。
 *
 * @author lang : 2025-12-22
 */
public abstract class MDActorOfApp extends MDActorOfModule {

    @Override
    protected Future<Boolean> startAsync(final MDConfiguration configuration, final Vertx vertxRef) {
        final HConfig config = configuration.inSetting();


        // 此处严格要求
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        if (Objects.isNull(config)) {
            logger.warn("[ XMOD ] 当前模块启用了应用配置却丢失配置，建议直接从 MDModuleActor 继承！{}", this.MID());
            return Future.succeededFuture(Boolean.TRUE);
        }


        // 子类实现，若有特殊模块信息则覆盖此方法
        final MDModuleRegistry registry = MDModuleRegistry.of(this.MID());
        return registry.afterApp(config, vertxRef).compose(initialized -> {


            // 此处 withRegistry 有注册过程，替换原始的 Pin.configure 方法的核心逻辑
            final HAmbient ambient = registry.withRegistry();

            return this.startAsync(ambient, vertxRef);
        });
    }

    /**
     * 交换参数，为了子类方法不重复，所以此处采用交换，将 {@link MDConfiguration} 参数置换出来，使用 {@link HAmbient} 代替！
     * 且此方法只能子类调用。
     *
     * @return 模块配置对象
     */
    protected MDConfiguration refConfiguration() {
        return OCacheConfiguration.of().valueGet(this.MID());
    }

    protected abstract Future<Boolean> startAsync(HAmbient ambient, Vertx vertxRef);
}
