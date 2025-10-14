package io.zerows.epoch.assembly;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.epoch.assembly.exception._40028Exception503DuplicatedImpl;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.platform.constant.VValue;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
@Slf4j
public class DI {
    private static final Cc<String, Object> CC_SINGLETON = Cc.open();
    private static final Cc<Class<?>, DI> CC_DI = Cc.open();
    private transient final Class<?> clazz;

    private DI(final Class<?> clazz) {
        this.clazz = clazz;
    }

    public static DI create(final Class<?> clazz) {
        return CC_DI.pick(() -> new DI(clazz), clazz);
    }

    // 直接创建一个新类
    public <T> T createInstance(final Class<?> clazz) {
        return (T) createProxy(clazz, null);
    }

    /**
     * 特殊单件模式处理，一方面要求 DI 的功能被开启，另外一方面为了保证不同的 {@link Named} 注解可实现唯一单件模式，
     * 这种情况用于一个 interface 中包含多个实现的单件模式处理，简单说单件不可用于接口判断，而是 impl 实现类判断，
     * 这样就可以保证 JSR 330 能够在 DI 部分更加灵活。
     *
     * @param clazz 类
     * @param <T>   类型
     *
     * @return 单例对象
     */
    // 直接创建一个单例
    public <T> T createSingleton(final Class<?> clazz) {
        final Injector di = DiFactory.singleton().build(); // ORepositoryClass.ofDI();
        /*
         * Add @Named Support
         */
        String extensionKey = this.named(clazz);
        return (T) CC_SINGLETON.pick(() -> di.getInstance(clazz), extensionKey);
    }

    public String named(final Class<?> clazz) {
        String name;
        if (clazz.isAnnotationPresent(Named.class)) {
            final Named annotation = clazz.getDeclaredAnnotation(Named.class);
            name = annotation.value();
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
        return instance;
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
            final VertxBootException error = new _40028Exception503DuplicatedImpl(interfaceCls);
            log.error("[ ZERO ] 异常发生 {}", error.getMessage());
            throw error;
        }
        // Null means direct interface only.
        return VValue.ONE == size ? filtered.get(VValue.IDX) : null;
    }
}
