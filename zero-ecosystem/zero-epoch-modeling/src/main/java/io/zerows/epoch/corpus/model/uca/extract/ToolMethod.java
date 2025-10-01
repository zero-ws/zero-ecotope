package io.zerows.epoch.corpus.model.uca.extract;

import io.r2mo.function.Fn;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.corpus.mature.exception._40007Exception500MethodNull;
import jakarta.ws.rs.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Method Resolver
 */
public class ToolMethod {

    private static final Annal LOGGER = Annal.get(ToolMethod.class);

    private static final ConcurrentMap<Class<?>, HttpMethod> METHODS =
        new ConcurrentHashMap<>() {
            {
                this.put(GET.class, HttpMethod.GET);
                this.put(POST.class, HttpMethod.POST);
                this.put(PUT.class, HttpMethod.PUT);
                this.put(DELETE.class, HttpMethod.DELETE);
                this.put(OPTIONS.class, HttpMethod.OPTIONS);
                this.put(HEAD.class, HttpMethod.HEAD);
                this.put(PATCH.class, HttpMethod.PATCH);
            }
        };

    @SuppressWarnings("all")
    public static HttpMethod resolve(final Method method) {
        // 1. Method checking.
        Fn.jvmKo(Objects.isNull(method), _40007Exception500MethodNull.class);
        final Annotation[] annotations = method.getDeclaredAnnotations();
        // 2. Method ignore
        HttpMethod result = null;
        for (final Annotation annotation : annotations) {
            final Class<?> key = annotation.annotationType();
            if (METHODS.containsKey(key)) {
                result = METHODS.get(key);
                break;
            }
        }
        // 2. Ignore this method.
        if (null == result) {
            LOGGER.debug(INFO.METHOD_IGNORE, method.getName());
        }
        return result;
    }

    public static boolean isValid(final Method method) {
        final int modifiers = method.getModifiers();
        final boolean valid = Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers);
        if (!valid) {
            LOGGER.debug(INFO.METHOD_MODIFIER, method.getName());
        }
        return valid;
    }
}
