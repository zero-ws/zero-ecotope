package io.zerows.epoch.assembly;

import com.google.inject.AbstractModule;
import io.zerows.epoch.web.Filter;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class DiGuiceFilter<T extends I, I> implements DiGuice<T, I> {

    private final transient Set<Class<?>> pointers = new HashSet<>();

    @Override
    public boolean success(final Class<?> clazz) {
        if (Filter.class.isAssignableFrom(clazz) &&
            !clazz.isInterface() &&
            !Modifier.isAbstract(clazz.getModifiers())) {
            this.pointers.add(clazz);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractModule module(final ConcurrentMap<Class<I>, Set<Class<T>>> tree,
                                 final Set<Class<T>> flat) {
        final Set<Class<?>> classes = this.pointers;
        return new DiGuiceModule() {
            @Override
            protected void configure() {
                if (classes.isEmpty()) {
                    return;
                }

                log.info("[ ZERO ] ( DI-H ) \uD83E\uDEBC Filter 扫描启动...");
                final Set<String> ignoreSet = new HashSet<>();
                classes.forEach(clazz -> {
                    final String bindCls = this.bindConstructor((Class<T>) clazz);
                    if (Objects.nonNull(bindCls)) {
                        ignoreSet.add(bindCls);
                    }
                });

                if (ignoreSet.isEmpty()) {
                    log.info("[ ZERO ] ( DI-H ) \uD83E\uDEBC Filter 扫描完成 Successfully !!!");
                } else {
                    log.info("[ ZERO ] ( DI-H ) \uD83E\uDEBC Filter 扫描完成 Successfully, 忽略项: {} !!!", Ut.fromJoin(ignoreSet));
                }
            }
        };
    }
}

