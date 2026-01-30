package io.zerows.extension.skeleton.metadata;

import io.zerows.epoch.web.MDConfiguration;

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

    private final String mid;
    private Y setting;
    private C config;

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
        return MDConfiguration.getOrCreate(this.mid);
    }

    protected String mid() {
        return this.mid;
    }
}
