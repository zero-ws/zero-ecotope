package io.zerows.epoch.corpus.model.uca.extract;

import io.r2mo.function.Fn;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.mature.exception._40009Exception500NoArgConstructor;
import io.zerows.epoch.corpus.mature.exception._40010Exception500AccessProxy;

import java.lang.reflect.Modifier;

public class ToolVerifier {

    public static void noArg(final Class<?> clazz) {
        Fn.jvmKo(!Ut.isDefaultConstructor(clazz), _40009Exception500NoArgConstructor.class, clazz);
    }

    public static void modifier(final Class<?> clazz) {
        Fn.jvmKo(!Modifier.isPublic(clazz.getModifiers()), _40010Exception500AccessProxy.class, clazz);
    }
}
