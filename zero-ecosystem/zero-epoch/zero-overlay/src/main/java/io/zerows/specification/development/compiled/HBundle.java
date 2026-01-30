package io.zerows.specification.development.compiled;

import java.net.URL;
import java.util.Objects;

/**
 * Bundle / 包
 * 针对不同环境提供功能包处理，此概念源起于 OSGI 中的 Bundle 模块化处理思想。
 * <pre>
 *     1. OSGI 环境中，{@link HBundle} 对应 OSGI 的 Bundle 模块，即使用 Bundle 提供相关实现。
 *     2. 非 OSGI 环境中，{@link HBundle} 对应功能包的概念，用于描述当前功能包的基本信息。
 *     3. 非 OSGI 环境会同时包含：本地功能包 / 分布式功能包 两种环境
 * </pre>
 *
 * @author lang : 2023-05-21
 */
public interface HBundle {

    // STATIC：接口中的静态方法

    /**
     * 计算缓存专用的 key 键值
     * <pre>
     *     1. 若 {@link HBundle} 为 null，则使用 {@link Class} 类名做缓存键
     *     2. 若 {@link HBundle} 不为 null，则根据模块实现提取缓存键
     * </pre>
     *
     * @param bundle 模块实例
     * @param clazz  核心组件类型
     * @return 缓存键
     */
    static String id(final HBundle bundle, final Class<?> clazz) {
        if (Objects.isNull(bundle)) {
            return clazz.getName();
        }
        return bundle.id(clazz);
    }

    // --------------------------------------------------------------

    /**
     * 热部署模式下用于处理缓存的 Key，此处和底层的 Bundle 对接，未来版本用于 OSIG 的基础桥接
     */
    String id(Class<?> clazz);

    /**
     * 库信息
     *
     * @return {@link HLibrary}
     */
    default HLibrary library() {
        return null;
    }

    /**
     * 资源目录
     *
     * @return {@link String}
     */
    default URL resource() {
        return this.resource(null);
    }

    default URL resource(final String path) {
        return null;
    }

    /**
     * 读取唯一名称，最终可以直接和 OSGI Bundle 的 SymbolicName 对接
     */
    String name();

    HBundle name(String name);
}
