package io.zerows.extension.skeleton.metadata;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.jigsaw.EquipAt;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.boot.ExAbstractHActor;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * Extension 模块抽象类，启动过程中用于加载模块的专用配置，构造 {@link MDConfiguration} 对象管理，此对象管理会处理如下事情：
 *
 * @author lang : 2025-11-04
 */
public abstract class MDModuleActor extends ExAbstractHActor {
    private static final OCacheConfiguration STORE = OCacheConfiguration.of();

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("启动扩展模块：{}", this.MID());
        // 创建一个新的 MDConfiguration
        final MDConfiguration configuration = this.createConfiguration();

        return this.startAsync(configuration, config, vertxRef);
    }

    protected Future<Boolean> startAsync(final MDConfiguration configuration,
                                         final HConfig config, final Vertx vertxRef) {
        // 子类实现，若有特殊模块信息则覆盖此方法
        return Future.succeededFuture(Boolean.TRUE);
    }

    private MDConfiguration createConfiguration() {
        MDConfiguration configuration = STORE.valueGet(this.MID());
        if (Objects.isNull(configuration)) {
            // 创建一个新的 MDConfiguration
            configuration = new MDConfiguration(this.MID());
            // 对新的 MDConfiguration 执行初始化 -> 写入到 MDConfiguration 对象中
            final EquipAt equipAt = EquipAt.of(configuration.id());
            equipAt.initialize(configuration);
            // 初始化完成之后写入缓存
            STORE.add(configuration);
        }
        return configuration;
    }

    protected abstract String MID();
}
