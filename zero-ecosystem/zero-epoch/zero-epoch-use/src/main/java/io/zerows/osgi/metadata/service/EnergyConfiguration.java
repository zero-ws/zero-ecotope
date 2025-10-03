package io.zerows.osgi.metadata.service;

import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置管理器，此实现在上层实现，主要用于管理 {@link MDConfiguration}，由于入口部分全部归到 {@link HSetting} 中，所以此处的配置会
 * 和 {@link HSetting} 一起来完成，两种配置的特征如下
 * <pre><code>
 *     1. HSetting -> 用来构造入口启动的专用配置
 *     2. MDConfiguration -> 用来构造模块化专用配置
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public interface EnergyConfiguration {

    ConcurrentMap<Long, HSetting> DATA_SETTING = new ConcurrentHashMap<>();

    // 添加配置
    @SuppressWarnings("all")
    EnergyConfiguration addConfiguration(MDConfiguration configuration);

    @SuppressWarnings("all")
    EnergyConfiguration addSetting(Bundle owner, HSetting setting);

    // 删除配置
    EnergyConfiguration removeConfiguration(MDConfiguration configuration);

    EnergyConfiguration removeSetting(Bundle owner);

    // 获取配置
    MDConfiguration getConfiguration(Bundle owner);

    HSetting getSetting(Bundle owner);
}
