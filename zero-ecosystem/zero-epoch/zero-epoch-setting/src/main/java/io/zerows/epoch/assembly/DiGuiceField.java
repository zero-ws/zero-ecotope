package io.zerows.epoch.assembly;

import com.google.inject.AbstractModule;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class DiGuiceField<T extends I, I> implements DiGuice<T, I> {
    private final transient Set<Class<?>> pointers = new HashSet<>();

    @Override
    public boolean success(final Class<?> clazz) {
        // Get all fields
        final Field[] fields = clazz.getDeclaredFields();
        final Set<Class<?>> extract = new HashSet<>();
        Arrays.stream(fields)
            .filter(field -> !Modifier.isStatic(field.getModifiers()))          // Ko Static
            // .filter(field -> !Modifier.isPublic(field.getModifiers()))       // Ko Non-Public
            .filter(field -> field.isAnnotationPresent(Inject.class))           // JSR 330
            .forEach(field -> extract.add(field.getType()));
        if (!extract.isEmpty()) {
            this.pointers.addAll(extract);
        }
        return !extract.isEmpty();
    }

    @Override
    @SuppressWarnings("all")
    public AbstractModule module(final ConcurrentMap<Class<I>, Set<Class<T>>> tree,
                                 final Set<Class<T>> flat) {
        final Set<Class<?>> classes = this.pointers;
        return new DiGuiceModule() {
            @Override
            protected void configure() {
                if (!classes.isEmpty()) {
                    log.info("[ ZERO ] ( DI ) \uD83E\uDEBC DI 依赖注入扫描启动...");
                    final Set<String> ignoreSet = new HashSet<>();
                    classes.forEach(clazz -> {
                        if (flat.contains(clazz)) {
                            // Standalone, Non-Constructor
                            final String bindCls = this.bindConstructor((Class<T>) clazz);
                            if (Objects.nonNull(bindCls)) {
                                ignoreSet.add(bindCls);
                            }
                        } else {
                            // Interface Part
                            if (clazz.isInterface()) {
                                final Set<Class<T>> implCls = tree.get(clazz);
                                final Set<String> ignored = this.bindInterface((Class<I>) clazz, implCls);
                                if (Objects.nonNull(ignored)) {
                                    ignoreSet.addAll(ignored);
                                }
                            }
                        }
                    });
                    if (ignoreSet.isEmpty()) {
                        log.info("[ ZERO ] ( DI ) \uD83E\uDEBC 字段扫描完成 Successfully !!!");
                    } else {
                        log.info("[ ZERO ] ( DI ) \uD83E\uDEBC 字段扫描完成 Successfully, 忽略项: {0} !!!", Ut.fromJoin(ignoreSet));
                    }
                }
            }
        };
    }
}

