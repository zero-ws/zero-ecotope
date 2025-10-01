package io.zerows.module.metadata.uca.scanner;

import io.zerows.epoch.constant.VString;
import org.osgi.framework.Bundle;

import java.net.URL;
import java.util.*;

/**
 * @author lang : 2024-05-02
 */
class ClassScannerBundle implements ClassScanner {
    @Override
    public Set<Class<?>> scan(final Bundle bundle) {
        Objects.requireNonNull(bundle);
        final String classPath = VString.SLASH;

        // 遍历所有类
        final Enumeration<URL> entities = bundle.findEntries(classPath, "*.class", true);
        final Set<Class<?>> classSet = new HashSet<>();
        while (entities.hasMoreElements()) {
            final URL url = entities.nextElement();
            final String path = url.getPath();
            final String className = path.substring(1, path.length() - 6).replace(VString.SLASH, VString.DOT);
            try {
                if (className.contains(VString.DASH) || Arrays.stream(ClassFilter.SKIP_PACKAGE).anyMatch(className::startsWith)) {
                    continue;
                }
                final Class<?> clazz = bundle.loadClass(className);
                if (Tool.isValid(clazz)) {
                    classSet.add(clazz);
                }
            } catch (final Throwable ex) {
                /*
                 * 此处保留异常用于调试，可在 JSR 299 以及其他反射扫描的位置使用和此相关的逻辑，当一个类无法被扫描到的时候，可以通过此处
                 * 语句呈现出来，方便调试，而不是直接导致程序本身不知道问题出在哪里
                 */
                this.logger().fatal(ex);
            }
        }
        return classSet;
    }
}
