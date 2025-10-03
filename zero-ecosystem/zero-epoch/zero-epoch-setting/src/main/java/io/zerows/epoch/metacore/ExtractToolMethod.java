package io.zerows.epoch.metacore;

import io.r2mo.function.Fn;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.metacore.exception._40007Exception500MethodNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Method Resolver
 */
@Slf4j
public class ExtractToolMethod {

    public static final String METHOD_IGNORE = "Method name = {} has not annotated with " +
        "jakarta.ws.rs.[@GET,@POST,@PUT,@DELETE,@OPTIONS,@PATCH,@HEAD], ignored resolving.";
    public static final String METHOD_MODIFIER = "( Ignored ) Method name = {} access scope is invalid, " +
        "the scope must be public non-static.";

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
            log.debug(METHOD_IGNORE, method.getName());
        }
        return result;
    }

    public static boolean isValid(final Method method) {
        final int modifiers = method.getModifiers();
        final boolean valid = Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers);
        if (!valid) {
            log.debug(METHOD_MODIFIER, method.getName());
        }
        return valid;
    }
}
