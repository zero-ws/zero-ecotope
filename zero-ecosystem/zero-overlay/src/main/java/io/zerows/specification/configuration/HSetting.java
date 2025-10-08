package io.zerows.specification.configuration;

/**
 * 🚀 @author lang : 2023-05-31
 */
public interface HSetting {

    // ====================== ⚙️ 容器配置区域 ======================

    /**
     * 🏗️ 返回容器配置
     *
     * @return {@link HConfig}
     */
    HConfig container();

    /**
     * 🛠️ 设置容器配置
     *
     * @param container 容器配置
     *
     * @return {@link HSetting}
     */
    HSetting container(HConfig container);


    // ====================== 🚀 启动器配置区域 ======================

    /**
     * 🚀 返回启动器配置
     *
     * @return {@link HConfig}
     */
    HConfig launcher();

    /**
     * 🔧 设置启动器配置
     *
     * @param launcher 启动器配置
     *
     * @return {@link HSetting}
     */
    HSetting launcher(HConfig launcher);


    // ====================== 🔌 插件配置区域 ======================

    /**
     * 🔌 设置插件配置
     *
     * @param name   插件名称
     * @param config 插件配置
     *
     * @return {@link HSetting}
     */
    HSetting infix(String name, HConfig config);

    /**
     * 🔍 返回插件配置
     *
     * @param name 插件名称
     *
     * @return {@link HConfig}
     */
    HConfig infix(String name);


    /**
     * 🔍 检查是否存在插件配置
     *
     * @param name 插件名称
     *
     * @return boolean 是否存在
     */
    boolean hasInfix(String name);

    // ====================== 🧩 扩展配置区域 ======================

    /**
     * 🧩 设置扩展配置
     *
     * @param name   扩展名称
     * @param config 扩展配置
     *
     * @return {@link HSetting}
     */
    HSetting extension(String name, HConfig config);

    /**
     * 📋 返回扩展配置
     *
     * @param name 扩展名称
     *
     * @return {@link HConfig}
     */
    HConfig extension(String name);
}