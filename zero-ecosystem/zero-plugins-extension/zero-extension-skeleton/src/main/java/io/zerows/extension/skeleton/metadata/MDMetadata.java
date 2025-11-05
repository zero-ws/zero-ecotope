package io.zerows.extension.skeleton.metadata;

import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.mbse.metadata.KColumn;
import io.zerows.mbse.metadata.KModule;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 替换之前的 AbstractBoot 中的核心数据结构，用于快速提取不同元数据相关信息，当前类只描述单个模块
 * <pre>
 *     1. 基本的模块配置信息 {@link MDConfiguration}
 *     2. 模块对应的 {@link KModule} 信息，服务于 CRUD 插件
 *     3. 加载的数据文件原始信息
 *     4. 对应的列配置信息 {@link KColumn}，服务于 UI 插件
 * </pre>
 *
 * @author lang : 2025-11-04
 */
@Slf4j
@Data
@Accessors(fluent = true, chain = true)
@Setter(AccessLevel.NONE)
public class MDMetadata implements Serializable {
    private final MDConfiguration configuration;
    private final ConcurrentMap<String, MDConnect> connect = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, KModule> modules = new ConcurrentHashMap<>();

    public MDMetadata(final MDConfiguration configuration) {
        this.configuration = configuration;
    }
}
