package io.zerows.epoch.assembly;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
@Slf4j
public class InquirerForGuice implements Inquirer<Injector> {
    private static final DiGuice jsrField = Ut.singleton(DiGuiceField.class);
    private static final DiGuice jsrMethod = Ut.singleton(DiGuiceMethod.class);
    private static final DiGuice jsrCon = Ut.singleton(DiGuiceConstructor.class);

    @Override
    @SuppressWarnings("all")
    public Injector scan(final Set<Class<?>> clazzes) {
        log.info("[ ZERO ] ( DI ) \uD83E\uDEBC DI 环境即将初始化, Total = `{}`", String.valueOf(clazzes.size()));
        /*
         * Scan start points, the condition is as following:
         * - 1. Contains member that annotated with @Inject
         * - 2. Constructor that annotated with @Inject
         * - 3. Method that annotated with @Inject
         */

        // The class that contains @Inject
        final Set<Class<?>> queueField = new HashSet<>();
        final Set<Class<?>> queueCon = new HashSet<>();
        final Set<Class<?>> queueMethod = new HashSet<>();
        // All interface queue
        final ConcurrentMap<Class<?>, Set<Class<?>>> tree = new ConcurrentHashMap<>();
        final Set<Class<?>> flat = new HashSet<>();
        clazzes.stream().filter(this::isValid).forEach(clazz -> {
            this.buildTree(tree, flat, clazz);
            if (!clazz.isInterface()) {
                if (jsrField.success(clazz)) {
                    queueField.add(clazz);
                } else if (jsrMethod.success(clazz)) {
                    queueMethod.add(clazz);
                } else if (jsrCon.success(clazz)) {
                    queueCon.add(clazz);
                }
            }
        });
        log.info("[ ZERO ] ( DI ) \uD83E\uDEBC 扫描信息 / field = {}, method = {}, constructor = {}",
            String.valueOf(queueField.size()), String.valueOf(queueMethod.size()), String.valueOf(queueCon.size()));

        // Implementation = Interface
        // Standalone

        return Guice.createInjector(
            this.jsrField.module(tree, flat),       // Field
            this.jsrCon.module(tree, flat),         // Constructor
            this.jsrMethod.module(tree, flat)       // Method
            // new JavaDi(implMap),        // Java Specification ( IMPL Mode )
            // new JsrDi(interfaceMap)     // Jsr Specification ( interface Map )
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
     *
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
