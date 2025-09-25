package io.zerows.core.web.model.uca.extract;

import io.zerows.core.fn.Fx;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.exception.BootAccessProxyException;
import io.zerows.core.web.model.exception.BootNoArgConstructorException;

import java.lang.reflect.Modifier;

public class ToolVerifier {

    public static void noArg(final Class<?> clazz, final Class<?> target) {
        final Annal logger = Annal.get(target);
        Fx.outBoot(!Ut.isDefaultConstructor(clazz), logger,
            BootNoArgConstructorException.class,
            logger, clazz);
    }

    public static void modifier(final Class<?> clazz, final Class<?> target) {
        final Annal logger = Annal.get(target);
        Fx.outBoot(!Modifier.isPublic(clazz.getModifiers()), logger,
            BootAccessProxyException.class,
            target, clazz);
    }
}
