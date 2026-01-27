package io.zerows.epoch.assembly;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.zerows.epoch.jigsaw.Inquirer;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
@Slf4j
public class InquirerForGuice implements Inquirer<Injector> {
    private static final DiGuice jsrField = Ut.singleton(DiGuiceField.class);
    private static final DiGuice jsrMethod = Ut.singleton(DiGuiceMethod.class);
    private static final DiGuice jsrCon = Ut.singleton(DiGuiceConstructor.class);
    private static final DiGuice jsrFilter = Ut.singleton(DiGuiceFilter.class);

    @Override
    @SuppressWarnings("all")
    public Injector scan(final Set<Class<?>> clazzes) {
        log.debug("[ ZERO ] ( DI ) \uD83E\uDEBC DI 环境即将初始化, Total = `{}`", String.valueOf(clazzes.size()));

        final AtomicInteger countField = new AtomicInteger(0);
        final AtomicInteger countMethod = new AtomicInteger(0);
        final AtomicInteger countCon = new AtomicInteger(0);
        final AtomicInteger countFilter = new AtomicInteger(0);
        // All interface queue
        final ConcurrentMap<Class<?>, Set<Class<?>>> tree = new ConcurrentHashMap<>();
        final Set<Class<?>> flat = new HashSet<>();
        clazzes.stream().filter(this::isValid)
            .filter(Predicate.not(Class::isInterface))
            .forEach(clazz -> {
                try {
                    this.buildTree(tree, flat, clazz);
                    if (jsrField.success(clazz)) {
                        countField.incrementAndGet();
                        return;
                    }

                    if (jsrMethod.success(clazz)) {
                        countMethod.incrementAndGet();
                        return;
                    }


                    if (jsrCon.success(clazz)) {
                        countCon.incrementAndGet();
                        return;
                    }

                    if (jsrFilter.success(clazz)) {
                        countFilter.incrementAndGet();
                        return;
                    }

                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        log.info("[ ZERO ] ( DI ) \uD83E\uDEBC 扫描信息 / F = {}, M = {}, C = {} / WebFilter = {}",
            countField.get(), countMethod.get(), countCon.get(),
            countFilter.get()
        );

        // Implementation = Interface
        // Standalone

        return Guice.createInjector(
            this.jsrField.module(tree, flat),       // Field        字段
            this.jsrCon.module(tree, flat),         // Constructor  构造函数
            this.jsrMethod.module(tree, flat),      // Method       方法
            this.jsrFilter.module(tree, flat)       // Filter       专用构造
        );
    }

    private void buildTree(final ConcurrentMap<Class<?>, Set<Class<?>>> tree,
                           final Set<Class<?>> flatSet,
                           final Class<?> clazz) {
        final Consumer<Class<?>> consumer = (item) -> {
            if (!tree.containsKey(item)) {
                tree.put(item, new HashSet<>());
            }
        };
        if (clazz.isInterface()) {
            consumer.accept(clazz);
        } else {
            final Class<?>[] interfacesCls = clazz.getInterfaces();
            if (0 == interfacesCls.length) {
                flatSet.add(clazz);
            } else {
                Arrays.stream(interfacesCls).forEach(interfaceCls -> {
                    consumer.accept(interfaceCls);
                    tree.get(interfaceCls).add(clazz);
                });
            }
        }
    }

    /**
     * 此处比底层的方法多做了一步，防止 NoClassDefFoundError 导致的异常，实现类在旧版中被 kill 了 No-Public 的类，这个是
     * 不对的，因为这里在实现类的注入过程中会出现使用 Provider 机制的创建，这种模式下实现类并不要求使用 public 的方式。特别是
     * {@link Defer} 注解的类，由于使用了 {@link jakarta.inject.Provider} 的方式构造，就更不要求 public 修饰符了，为了
     * 保证包本身的封装型，default 域也是可使用这种方式初始化的，所以此处的过滤条件需要放宽。
     * <pre>
     *     旧版 public 引起的问题
     *     [Guice/MissingImplementation] : No implementation for ??? was bound.
     * </pre>
     *
     * @param clazz 实现类
     * @return 是否合法
     */
    private boolean isValid(final Class<?> clazz) {
        // java.lang.NoClassDefFoundError
        final Class<?> existing = Ut.clazz(clazz.getName(), null);
        if (Objects.isNull(existing)) {
            return false;
        }
        return ClassFilter.isValid(clazz);
    }
}
