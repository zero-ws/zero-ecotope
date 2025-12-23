package io.zerows.epoch.basicore;

import java.io.Serializable;

/**
 * 核心配置接口，用于处理模块的配置扩展，挂载配置，执行 vertx.yml 中的配置扩展专用，所有模块扩展的配置类都应该实现此接口，模块配置包含两部分
 * <pre>
 *     1. vertx.yml 中的系统级模块配置
 *     2. plugins/{mid}/configuration.json 应用级模块配置
 * </pre>
 *
 * @author lang : 2025-12-23
 */
public interface MDConfig extends Serializable {
}
