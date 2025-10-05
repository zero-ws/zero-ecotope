package io.zerows.epoch.boot;

import io.zerows.epoch.basicore.YmConfiguration;

/**
 * 配置程序客户端，替换旧版的 {@see OZeroStore} 配置信息，此处的配置客户端主要用于进行拆分从本地或远程加载相关配置
 * <pre>
 *     1. 独立程序配置（不访问 Nacos）-> vertx.yml
 *     2. 远程配置程序（访问 Nacos）-> vertx-boot.yml
 *        - 远程共享配置
 *        - 远程私有配置
 *        - 本地私有配置
 *     3. 远程的优先级配置如：本地私有配置 > 远程私有配置 > 远程共享配置
 * </pre>
 *
 * @author lang : 2025-10-06
 */
public interface ConfigClient {
    
    YmConfiguration build();
}
