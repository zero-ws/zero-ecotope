package io.zerows.extension.skeleton.metadata;

import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.jigsaw.EquipAt;
import io.zerows.epoch.management.OCacheConfiguration;

import java.util.Objects;

/**
 * 统一管理三层配置
 * <pre>
 *     1. YAML 配置：对应 vertx.yml 中的模块配置段
 *     2. CONFIG 配置：对应 plugins/{mid}/configuration.json 中的模块
 *     3. MDConfiguration 配置：对应模块的综合配置对象，包含前两者
 * </pre>
 *
 * @author lang : 2025-12-23
 */
public abstract class MDModuleManager<Y, C> implements MDManager<Y, C> {

    private static final OCacheConfiguration STORE = OCacheConfiguration.of();
    private Y setting;
    private C config;
    private final String mid;

    protected MDModuleManager(final String mid) {
        this.mid = mid;
    }

    @Override
    public void setting(final Y yaml) {
        this.setting = yaml;
    }

    @Override
    public Y setting() {
        return this.setting;
    }

    @Override
    public void config(final C config) {
        this.config = config;
    }

    @Override
    public C config() {
        return this.config;
    }

    @Override
    public MDConfiguration configuration() {
        MDConfiguration configuration = STORE.valueGet(this.mid);
        if (Objects.isNull(configuration)) {
            // 创建一个新的模块配置
            configuration = new MDConfiguration(this.mid);
            // 对新的 MDConfiguration 执行初始化 -> 写入到 MDConfiguration 对象中
            final EquipAt equipAt = EquipAt.of(configuration.id());
            equipAt.initialize(configuration);
            // 初始化完成后追加
            STORE.add(configuration);
        }
        return configuration;
    }
}
