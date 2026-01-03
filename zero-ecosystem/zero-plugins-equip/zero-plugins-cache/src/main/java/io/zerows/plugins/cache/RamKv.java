package io.zerows.plugins.cache;

import io.r2mo.jaas.session.UserAt;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 旧版 Rapid 的替代品，底层访问
 * <pre>
 *     1. 当前组件创建 {@link HMM}，其中 {@link HMM} 为开发人员可使用的直接工具组件
 *     2. {@link HMM} 内部依赖会对初始化的缓存进行选择：
 *        - 原生：{@link SharedClient}，根据部署环境自动选择实现（集群环境和本地环境）
 *        - 扩展：{@link CachedClient}，通过 SPI 机制加载对应的缓存实现
 *               - 如果 SPI 是 EhCache 其核心实现流程还会多一层哈希表选择，因为 EhCache 需要在初始化时指定更多参数
 *     3. 现阶段主要针对三种数据类型
 *        - String = {@link UserAt}
 *        - String = {@link JsonObject} 配置 / 全局数据
 *        - STRING = {@link JsonArray} 字典类型数据
 * </pre>
 */
public interface RamKv {
    
}
