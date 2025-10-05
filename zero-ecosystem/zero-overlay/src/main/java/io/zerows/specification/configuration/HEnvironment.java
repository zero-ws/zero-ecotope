package io.zerows.specification.configuration;

import java.util.Set;

/**
 * 环境变量管理器，用于管理和访问应用程序的环境变量，移除旧版的 System.getenv() 直接访问模式，不同场景的环境变量
 * 可能会有所区别，主要在于 {@see Up} 注解中启动的注释选择，核心在于
 * <pre>
 *     1. 带有 Nacos 配置程序的环境变量管理
 *     2. 不带有 Nacos 配置程序的环境变量管理
 * </pre>
 * 启动器中会优先考虑环境变量的注入和使用
 *
 * @author lang : 2025-10-05
 */
public interface HEnvironment {
    /**
     * 获取指定键的环境变量值
     *
     * @param key 环境变量的键
     *
     * @return 环境变量的值，如果不存在则返回 null
     */
    String get(String key);

    Integer getInt(String key);

    /**
     * 获取所有环境变量的键集合
     *
     * @return 环境变量键的集合
     */
    Set<String> vars();
}
