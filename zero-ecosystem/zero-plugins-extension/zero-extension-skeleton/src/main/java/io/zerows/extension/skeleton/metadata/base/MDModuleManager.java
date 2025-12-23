package io.zerows.extension.skeleton.metadata.base;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.jigsaw.EquipAt;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.metadata.MDSetting;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 元数据管理器，用于管理所有的配置实例
 * <pre>
 *     1. 核心实例 {@link MDConfiguration}
 * </pre>
 *
 * @author lang : 2025-11-05
 */
class MDModuleManager {

    private static final OCacheConfiguration STORE = OCacheConfiguration.of();
    private static final Cc<String, MDModuleManager> CC_MANAGER = Cc.open();
    /**
     * 特殊数据结构，用于存储使用 {@link MDSetting} 进行配置之后转换的模块专用配置对象，提取过程中只能直接从 {@link HActor} 中提取，且使用静态继承模式，
     * 即父类使用 protected 的方式开放 static 方法，而子类可调用的模式下将其改成 public 进行提取，唯一有一点需要注意，static 方法不支持重写，所以只能
     * 使用隐藏的方式进行覆盖！！！
     */
    private static final ConcurrentMap<Class<?>, Object> CONFIG_MAP = new ConcurrentHashMap<>();
    private final String mid;

    private MDModuleManager(final String mid) {
        this.mid = mid;
    }

    static MDModuleManager of(final String mid) {
        Objects.requireNonNull(mid, "[ XMOD ] 模块 ID 不可以为 null！");
        return CC_MANAGER.pick(() -> new MDModuleManager(mid), mid);
    }

    void registry(final Class<?> key, final Object config) {
        if (Objects.isNull(config)) {
            return;
        }
        CONFIG_MAP.putIfAbsent(key, config);
    }

    @SuppressWarnings("unchecked")
    <T> T getConfig(final Class<?> key) {
        return (T) CONFIG_MAP.getOrDefault(key, null);
    }

    MDConfiguration registry(final HConfig launcherMod) {
        MDConfiguration configuration = STORE.valueGet(this.mid);
        if (Objects.isNull(configuration)) {
            // 创建一个新的模块配置
            configuration = new MDConfiguration(this.mid);
            // 对新的 MDConfiguration 执行初始化 -> 写入到 MDConfiguration 对象中
            // 对新的 MDConfiguration 执行初始化 -> 写入到 MDConfiguration 对象中
            final EquipAt equipAt = EquipAt.of(configuration.id());
            equipAt.initialize(configuration);
            // 执行模块主配置
            if (Objects.nonNull(launcherMod)) {
                configuration.addConfig(launcherMod);
            }
            // 初始化完成之后写入缓存
            STORE.add(configuration);
        }
        return configuration;
    }
}
