package io.zerows.epoch.assembly;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.r2mo.function.Fn;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-04-17
 */
@Slf4j
@SuppressWarnings("all")
class ClassScannerCommon implements ClassScanner {

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // 保证线程安全
        final Set<Class<?>> classSet = Collections.synchronizedSet(new HashSet<>());

        Fn.jvmAt(() -> {
            final ClassPath cp = ClassPath.from(loader);
            final ImmutableSet<ClassPath.ClassInfo> set = cp.getTopLevelClasses();
            final ConcurrentMap<String, Set<String>> packageMap = new ConcurrentHashMap<>();
            // 性能提高一倍，并行流处理更合理，暂时没发现明显问题
            log.info("[ ZERO ] 忽略的包数量: {}, 检查是否跳过？", String.valueOf(ClassFilter.SKIP_PACKAGE.length));
            set.stream().forEach(cls -> {
                final String packageName = cls.getPackageName();
                final boolean skip = Arrays.stream(ClassFilter.SKIP_PACKAGE).anyMatch(packageName::startsWith);
                if (!skip) {
                    try {
                        classSet.add(loader.loadClass(cls.getName()));
                    } catch (final Throwable ex) {

                    }
                }
            });
        });

        // 过滤合法的 Class
        return classSet.stream()
            .filter(Tool::isValid)
            .collect(Collectors.toSet());
    }
}
