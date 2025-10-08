package io.zerows.specification.configuration;

import io.zerows.platform.enums.EmBoot;

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


    // ====================== 🚀 启动器配置区域 ======================

    /**
     * 🚀 返回启动器配置
     *
     * @return {@link HConfig}
     */
    HConfig launcher();


    HConfig boot(EmBoot.LifeCycle lifeCycle);

    // ====================== 🔌 插件配置区域 ======================

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
    @Deprecated
    default boolean hasInfix(final String name) {
        return false;
    }

    // ====================== 🧩 扩展配置区域 ======================

    /**
     * 📋 返回扩展配置
     *
     * @param name 扩展名称
     *
     * @return {@link HConfig}
     */
    HConfig extension(String name);
}