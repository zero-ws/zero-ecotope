package io.zerows.epoch.assembly;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.zerows.support.Ut;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public abstract class DiGuiceModule extends AbstractModule {

    protected <T> String bindConstructor(final Class<T> clazz) {
        // Standalone, Non-Constructor
        if (Ut.isDefaultConstructor(clazz)) {
            final Constructor<T> constructor = Ut.constructor(clazz);
            if (clazz.isAnnotationPresent(Singleton.class)) {
                this.bind(clazz).toConstructor(constructor).asEagerSingleton();
            } else {
                this.bind(clazz).toConstructor(constructor);
            }
            log.info("[ ZERO ] ( DI ) 构造函数: `{}`", clazz);
            return null;
        } else {
            return clazz.getName();
        }
    }

    @SuppressWarnings("all")
    protected <T extends I, I> Set<String> bindInterface(final Class<I> interfaceCls, final Set<Class<T>> implSet) {
        if (!implSet.isEmpty()) {
            final Set<String> clazzSet = new HashSet<>();
            if (1 == implSet.size()) {
                final Class<T> clazz = implSet.iterator().next();
                this.bind(interfaceCls).to(clazz);
                log.info("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`", clazz.getName(), interfaceCls.getName());
                // clazzSet.add(clazz.getName());
            } else {
                // interface with multi classed injection
                implSet.forEach(implCls -> {
                    if (implCls.isAnnotationPresent(Named.class)) {
                        final Annotation annotation = implCls.getAnnotation(Named.class);
                        final String name = Ut.invoke(annotation, "get");
                        log.info("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`, 标识 = {}",
                            implCls.getName(), interfaceCls.getName(), name);
                        this.bind(interfaceCls).annotatedWith(Names.named(name))
                            .to(implCls);
                    } else {
                        clazzSet.add(implCls.getName());
                    }
                });
            }
            return clazzSet;
        } else {
            return null;
        }
    }
}
