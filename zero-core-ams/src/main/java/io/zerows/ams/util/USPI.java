package io.zerows.ams.util;

import io.zerows.core.exception.internal.SPINullException;

import java.util.*;

/**
 * @author lang : 2023/4/27
 */
final class USPI {

    private USPI() {
    }

    static <T> Collection<T> services(final Class<T> clazz) {
        return services(clazz, null);
    }

    static <T> Collection<T> services(final Class<T> clazz, final ClassLoader classLoader) {
        final List<T> list = new ArrayList<>();
        ServiceLoader<T> factories;
        if (classLoader != null) {
            /*
             * 1. 外部传入了 ClassLoader，这种情况直接使用外部的 ClassLoader 加载
             */
            factories = ServiceLoader.load(clazz, classLoader);
        } else {
            /*
             * 2. 外部没有传入 ClassLoader，则直接使用 TCCL 加载
             */
            final ClassLoader TCCL = Thread.currentThread().getContextClassLoader();
            factories = ServiceLoader.load(clazz, TCCL);
        }
        if (factories.iterator().hasNext()) {
            /* 「OK」上边二选一已拿到信息，则直接遍历提取 */
            factories.iterator().forEachRemaining(list::add);
            return list;
        }


        /*
         * 3. 默认使用 TCCL，但在 OSGi 环境中可能不够，因此尝试使用加载此类的类加载器，所以为了兼容 osgi 环境，需要使用
         *    - clazz 的类加载器
         *    - USPI.class 的类加载器
         */
        final ClassLoader clazzLoader = clazz.getClassLoader();
        factories = ServiceLoader.load(clazz, clazzLoader);
        if (factories.iterator().hasNext()) {
            /* 「OK」上边已拿到信息，则直接遍历提取 */
            factories.iterator().forEachRemaining(list::add);
            return list;
        }


        /*
         * 4. 直接使用 USPI.class 的类加载器（特殊情况）
         */
        final ClassLoader spiLoader = USPI.class.getClassLoader();
        factories = ServiceLoader.load(clazz, spiLoader);
        if (factories.iterator().hasNext()) {
            factories.iterator().forEachRemaining(list::add);
            return list;
        }

        return Collections.emptyList();
    }

    static <T> T service(final Class<T> interfaceCls, final Class<?> caller, final boolean strict) {
        final ClassLoader loader = Optional.ofNullable(caller).map(Class::getClassLoader).orElse(null);
        return service(interfaceCls, loader, strict);
    }

    static <T> T service(final Class<T> interfaceCls, final ClassLoader loader, final boolean strict) {
        final Collection<T> collection = services(interfaceCls, loader);
        final T service;
        if (!collection.isEmpty()) {
            service = collection.iterator().next();
        } else {
            service = null;
        }
        if (Objects.isNull(service) && strict) {
            throw new SPINullException(USPI.class);
        }
        return service;
    }
}
