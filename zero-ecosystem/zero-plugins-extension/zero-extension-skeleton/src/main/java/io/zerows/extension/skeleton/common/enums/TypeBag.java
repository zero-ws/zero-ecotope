package io.zerows.extension.skeleton.common.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum TypeBag {
    /**
     * --> 纯技术功能
     * 内核模块不带有任何表功能，通常会内置功能模块，如
     * <pre>
     *     - 核心容器层的功能组件
     *     - 插件层 zero-plugins-xxx 中的核心功能组件
     * </pre>
     * 这种模块的特点在于无任何表结构，只是内置功能
     */
    KERNEL("Z-KERNEL"),             // Z-KERNEL         // Zero内核功能模块（第三方核心驱动模块）

    /**
     * --> 横向业务
     * 基础模块主要用于描述 Zero 提供的标准化横向处理功能模块，如
     * <pre>
     *     - 工作流
     *     - Office Doc文档管理
     *     - 集成管理
     *     - 图引擎
     *     - MBSE动态建模
     *     - CRUD动态接口植入
     * </pre>
     * 这些模块一般不包含任何业务信息，横跨整个框架标准化底层，为其他所有模块提供强化的能力支撑
     */
    FOUNDATION("Z-FOUNDATION"),     // Z-FOUNDATION     // Zero基础功能模块，核心框架专用模块

    /**
     * --> 垂直业务
     * zero-exmodule-xxx 扩展模块中的标准化带业务表结构的核心模块，如
     * <pre>
     *     - 系统管理     zero-exmodule-ambient
     *     - 安全管理     zero-exmodule-rbac
     *     - 集成管理     zero-exmodule-integration
     *     - 组织架构     zero-exmodule-erp
     *     ....
     * </pre>
     * 标准化业务模块，自带接口、表结构等
     */
    COMMERCE("Z-COMMERCE"),         // Z-COMMERCE       // Zero商业功能模块，标准化商业功能模块

    EXTENSION("EXTENSION");         // EXTENSION

    private static final ConcurrentMap<String, TypeBag> TYPE_MAP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(TypeBag.values()).forEach(wall -> TYPE_MAP.put(wall.key(), wall));
    }

    private transient final String value;

    TypeBag(final String value) {
        this.value = value;
    }

    public static TypeBag from(final String configKey) {
        return TYPE_MAP.getOrDefault(configKey, null);
    }

    public static Set<String> keys() {
        return TYPE_MAP.keySet();
    }

    public String key() {
        return this.value;
    }
}
