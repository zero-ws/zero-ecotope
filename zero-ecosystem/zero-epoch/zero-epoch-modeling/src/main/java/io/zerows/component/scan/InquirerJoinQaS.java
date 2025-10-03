package io.zerows.component.scan;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.QaS;
import io.zerows.platform.metadata.KRunner;
import io.zerows.support.Ut;
import io.zerows.sdk.environment.Inquirer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class InquirerJoinQaS implements Inquirer<ConcurrentMap<String, Method>> {

    @Override
    public ConcurrentMap<String, Method> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> qas = clazzes.stream()
            .filter((item) -> item.isAnnotationPresent(QaS.class))
            .collect(Collectors.toSet());
        // address = Method
        final ConcurrentMap<String, Method> result = new ConcurrentHashMap<>();
        KRunner.run(qas, clazz -> result.putAll(this.scan(clazz)));
        this.logger().info(INFO.HQAS, qas.size(), result.keySet());
        return result;
    }

    private ConcurrentMap<String, Method> scan(final Class<?> clazz) {
        final ConcurrentMap<String, Method> result = new ConcurrentHashMap<>();

        final Method[] methods = clazz.getDeclaredMethods();
        Arrays.stream(methods)
            .filter(this::isValid)
            .filter(method -> method.isAnnotationPresent(Address.class))
            .forEach(method -> {
                final Annotation address = method.getAnnotation(Address.class);
                final String value = Ut.invoke(address, "value");
                result.put(value, method);
            });
        return result;
    }

    private boolean isValid(final Method method) {
        final int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers);
    }
}
