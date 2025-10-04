package io.zerows.specification.configuration;

import io.zerows.platform.enums.EmApp;

/**
 * 🚀 启动配置接口 - 应用程序启动的核心配置组件
 *
 * @author lang : 2023-05-31
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HBoot {

    /**
     * 🏷️ 获取应用类型标识符
     * 📋 支持的应用类型：
     * <pre><code>
     *     🟢 APPLICATION: 🖥️  单独应用程序
     *     🔵 SERVICE:     🛠️  微服务组件后端
     *     🔴 GATEWAY:     🌉  微服务组件网关
     * </code></pre>
     *
     * @return 🎯 {@link EmApp.Type} - 应用类型枚举
     * @see EmApp.Type 应用类型枚举定义
     */
    EmApp.Type app();

    /**
     * 🎛️ 设置应用类型配置
     *
     * @param type 🏷️ {@link EmApp.Type} - 要设置的应用类型
     *
     * @return 🔄 {@link HBoot} - 链式调用返回当前实例
     */
    HBoot app(EmApp.Type type);

    /**
     * 🔗 绑定启动主类和命令行参数
     * 📝 参数映射关系：
     * <pre><code>
     *     🎯 mainClass -> target    📁 启动目标类
     *     📋 arguments -> args      📝 启动参数数组
     * </code></pre>
     *
     * @param mainClass 🎯 启动主类，应用程序的入口点
     * @param arguments 📋 可变参数数组，启动时传递的命令行参数
     *
     * @return 🔄 {@link HBoot} - 链式调用返回当前实例
     * @throws IllegalArgumentException 🚨 当主类为 null 时抛出异常
     * @since 1.0.0
     */
    HBoot bind(Class<?> mainClass, String... arguments);

    /**
     * 📋 获取当前启动的参数数组
     *
     * @return 📝 {@link String[]} - 启动时的命令行参数数组
     */
    String[] args();

    /**
     * 🎯 获取当前启动的目标主类
     *
     * @return 🏷️ {@link Class} - 启动的主类对象
     */
    Class<?> target();

    /**
     * 🚀 从系统中获取启动器类
     * 📌 启动器类通常是框架内部使用的启动类
     *
     * @return 🏷️ {@link Class} - 系统启动器类
     * @since 1.0.0
     */
    Class<?> launcher();

    /**
     * ⚡ 获取本次启动的能量配置
     * 💫 能量配置包含启动时的各种配置参数和设置
     *
     * @return ⚙️ {@link HEnergy} - 启动能量配置对象
     * @see HEnergy 能量配置接口定义
     */
    HEnergy energy();
}