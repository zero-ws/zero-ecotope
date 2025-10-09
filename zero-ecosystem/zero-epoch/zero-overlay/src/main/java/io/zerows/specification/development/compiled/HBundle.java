package io.zerows.specification.development.compiled;

import java.net.URL;
import java.util.Objects;

/**
 * 「Bundle」Bundle
 * <hr/>
 * 针对OSGI的打包专用 Bundle 部分，可作为插件底层，而其他所有的内容都是从 Bundle 中直接引用而来。
 *
 * @author lang : 2023-05-21
 */
public interface HBundle {
    /**
     * 库信息
     *
     * @return {@link HLibrary}
     */
    default HLibrary library() {
        return null;
    }

    /**
     * 资源目录信息
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
     * 热部署模式下用于处理缓存的 Key，此处和底层的 Bundle 对接，未来版本用于 OSIG 的基础桥接
     */
    String id(Class<?> clazz);

    /**
     * 读取唯一名称，最终可以直接和 OSGI Bundle 的 SymbolicName 对接
     */
    String name();

    HBundle name(String name);

    static String id(final HBundle bundle, final Class<?> clazz) {
        if (Objects.isNull(bundle)) {
            return clazz.getName();
        }
        return bundle.id(clazz);
    }
}
