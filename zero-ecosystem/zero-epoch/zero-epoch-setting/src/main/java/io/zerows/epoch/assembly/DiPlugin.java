package io.zerows.epoch.assembly;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.component.log.OLog;
import io.zerows.epoch.assembly.exception._40028Exception503DuplicatedImpl;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
@Slf4j
public class DiPlugin {

    private static final Cc<Class<?>, DiPlugin> CC_DI = Cc.open();
    private transient final Class<?> clazz;
    private transient final DiInfix infix;
    private transient final OLog logger;

    private DiPlugin(final Class<?> clazz) {
        this.clazz = clazz;
        this.logger = Ut.Log.metadata(clazz);
        this.infix = new DiInfix(clazz);
    }

    public static DiPlugin create(final Class<?> clazz) {
        return CC_DI.pick(() -> new DiPlugin(clazz), clazz);
        // return FnZero.po?l(Pool.PLUGINS, clazz, () -> new DiPlugin(clazz));
    }

    // 直接创建一个新类
    public <T> T createInstance(final Class<?> clazz) {
        return (T) createProxy(clazz, null);
    }

    // 直接创建一个单例
    public <T> T createSingleton(final Class<?> clazz) {
        final Injector di = DiFactory.singleton().build(); // ORepositoryClass.ofDI();
        /*
         * Add @Named Support
         */
        String extensionKey = this.named(clazz);
        return Ut.singleton(clazz,
            () -> (T) this.infix.wrapInfix(di.getInstance(clazz)),
            extensionKey);
    }

    public String named(final Class<?> clazz) {
        String name;
        if (clazz.isAnnotationPresent(Named.class)) {
            final Annotation annotation = clazz.getAnnotation(Named.class);
            name = Ut.invoke(annotation, "value");
        } else {
            name = null;
        }
        return name;
    }

    // 创建一个新的
    public Object createProxy(final Class<?> clazz, final Method action) {
        final Injector di = DiFactory.singleton().build();
        final Object instance;
        if (clazz.isInterface()) {
            final Class<?> implClass = uniqueChild(clazz);
            if (null != implClass) {
                // Interface + Impl
                instance = di.getInstance(clazz); // Ut.singleton(implClass);
            } else {
                /*
                 * SPEC5: Interface only, direct api, in this situation,
                 * The proxy is null and the agent do nothing. The request will
                 * send to event bus direct. It's not needed to set
                 * implementation class.
                 */
                instance = DiProxyInstance.create(clazz);
            }
        } else {
            if (Objects.isNull(action)) {
                instance = di.getInstance(clazz);
            } else {
                instance = di.getInstance(action.getDeclaringClass());
            }
        }
        return this.infix.wrapInfix(instance);
    }

    private Class<?> uniqueChild(final Class<?> interfaceCls) {
        final Set<Class<?>> classes = OCacheClass.entireValue();
        final List<Class<?>> filtered = classes.stream()
            .filter(item -> interfaceCls.isAssignableFrom(item)
                && item != interfaceCls)
            .toList();
        final int size = filtered.size();
        // Non-Unique throw error out.
        if (VValue.ONE < size) {
            // final BootingException error = new BootDuplicatedImplException(Instance.class, interfaceCls);
            final VertxBootException error = new _40028Exception503DuplicatedImpl(interfaceCls);
            log.error("[ ZERO ] 异常发生 {}", error.getMessage());
            throw error;
        }
        // Null means direct interface only.
        return VValue.ONE == size ? filtered.get(VValue.IDX) : null;
    }
}
