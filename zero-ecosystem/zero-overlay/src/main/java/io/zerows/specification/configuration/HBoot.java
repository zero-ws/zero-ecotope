package io.zerows.specification.configuration;

import io.zerows.platform.enums.EmApp;

/**
 * 🚀 启动配置接口 - 应用程序启动的核心配置组件
 * <pre>
 *     🎯 设计意图：
 *     - 🏗️ 统一应用程序启动过程中的所有元数据管理
 *     - 🔄 提供完整的启动生命周期配置支持
 *     - 🔗 连接不同启动阶段的配置组件（Pre/On/Run/Off）
 *     - 📋 支持多种应用类型和启动模式
 *
 *     📁 配置结构：
 *     boot:
 *       ├── input: 输入配置
 *       ├── container: 容器配置
 *       └── lifecycle: 生命周期组件
 * </pre>
 *
 * @author lang : 2023-05-31
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HBoot {

    // ====================== 📥 输入和自动计算部分 ======================

    /**
     * 🏷️ 获取应用类型标识符
     * 📋 支持的应用类型：
     * <pre>
     *     🟢 APPLICATION: 🖥️  单独应用程序 - 传统单体应用
     *     🔵 SERVICE:     🛠️  微服务组件后端 - 云原生微服务
     *     🔴 GATEWAY:     🌉  微服务组件网关 - API 网关服务
     * </pre>
     * <pre>
     *     🎯 设计意图：
     *     - 根据应用类型选择不同的启动策略
     *     - 为不同应用类型提供定制化的启动配置
     *     - 支持应用类型的动态扩展和识别
     * </pre>
     *
     * @return 🎯 {@link EmApp.Type} - 应用类型枚举
     * @see EmApp.Type 应用类型枚举定义
     */
    EmApp.Type app();

    /**
     * 📋 获取当前启动的参数数组
     * <pre>
     *     🎯 设计意图：
     *     - 提供启动时的命令行参数访问
     *     - 支持参数的动态传递和解析
     *     - 为启动过程提供上下文信息
     * </pre>
     *
     * @return 📝 {@link String[]} - 启动时的命令行参数数组
     */
    String[] inArgs();

    /**
     * 🎯 获取当前启动的目标主类
     * <pre>
     *     🎯 设计意图：
     *     - 确定启动入口点
     *     - 支持主类的动态配置和替换
     *     - 为类加载器提供正确的启动类
     * </pre>
     *
     * @return 🏷️ {@link Class} - 启动的主类对象
     */
    Class<?> inMain();

    // ====================== 🚀 容器部分双设计 ======================

    /**
     * 🚀 从系统中获取启动器类
     * 📌 启动器类通常是框架内部使用的启动类
     * <pre>
     *     🎯 设计意图：
     *     - 提供统一的启动器访问接口
     *     - 支持不同容器类型的启动器（Vert.x、Jetty、HAeon等）
     *     - 为启动过程提供容器抽象层
     * </pre>
     *
     * @return 🏷️ {@link HLauncher} - 系统启动器实例
     * @since 1.0.0
     */
    <C> HLauncher<C> launcher();

    /**
     * 🛠️ 预启动配置组件
     * <pre>
     *     🎯 设计意图：
     *     - 提供启动前的预处理配置
     *     - 支持启动器的前置初始化操作
     *     - 为容器启动前的准备工作提供配置支持
     * </pre>
     *
     * ⚠️ 注意：泛型 C 代表 Container（容器），与 HLauncher 中的容器类型一致
     *
     * @return 🧩 {@link HLauncher.Pre} - 预启动组件
     */
    <C> HLauncher.Pre<C> withPre();

    // ====================== 🔄 生命周期组件 ======================

    /**
     * ⚡ 启动时配置组件
     * <pre>
     *     🎯 设计意图：
     *     - 处理应用启动过程中的核心配置
     *     - 管理启动时的依赖注入和组件初始化
     *     - 为应用启动提供完整的配置上下文
     * </pre>
     *
     * ⚠️ 注意：泛型 T 代表 HConfig 的子类，表示配置类型
     *
     * @return 🧩 {@link HConfig.HOn} - 启动配置组件
     */
    <T extends HConfig> HConfig.HOn<T> whenOn();

    /**
     * 🔄 运行时配置组件
     * <pre>
     *     🎯 设计意图：
     *     - 管理应用运行期间的动态配置
     *     - 支持运行时的配置更新和刷新
     *     - 提供运行时环境的配置管理
     * </pre>
     *
     * ⚠️ 注意：泛型 T 代表 HConfig 的子类，表示配置类型
     *
     * @return 🧩 {@link HConfig.HRun} - 运行配置组件
     */
    <T extends HConfig> HConfig.HRun<T> whenRun();

    /**
     * 🛑 停止时配置组件
     * <pre>
     *     🎯 设计意图：
     *     - 处理应用停止时的清理工作
     *     - 管理资源释放和状态保存
     *     - 确保优雅停机和状态持久化
     * </pre>
     *
     * ⚠️ 注意：泛型 T 代表 HConfig 的子类，表示配置类型
     *
     * @return 🧩 {@link HConfig.HOff} - 停止配置组件
     */
    <T extends HConfig> HConfig.HOff<T> whenOff();
}