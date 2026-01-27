package io.zerows.extension.skeleton.metadata;

import io.zerows.epoch.web.MDConfiguration;
import io.zerows.specification.development.compiled.HBundle;

/**
 * 模块配置管理器统一接口（主要用于统一接口对配置进行读写）
 *
 * @author lang : 2025-12-22
 */
public interface MDManager<YAML, CONFIG> {

    void setting(YAML yaml);

    YAML setting();

    void config(CONFIG config);

    CONFIG config();

    MDConfiguration configuration();

    default boolean isEnabled(final HBundle owner) {
        return true;
    }
}
