package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.zerows.epoch.assembly.exception._40009Exception500NoArgConstructor;
import io.zerows.epoch.assembly.exception._40010Exception500AccessProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ExtractTool {

    public static void verifyNoArgConstructor(final Class<?> clazz) {
        Fn.jvmKo(!isDefaultConstructor(clazz), _40009Exception500NoArgConstructor.class, clazz);
    }

    static boolean isDefaultConstructor(final Class<?> clazz) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return Arrays.stream(constructors)
            .anyMatch(constructor -> 0 == constructor.getParameterTypes().length);
    }

    public static void verifyIfPublic(final Class<?> clazz) {
        Fn.jvmKo(!Modifier.isPublic(clazz.getModifiers()), _40010Exception500AccessProxy.class, clazz);
    }
}
