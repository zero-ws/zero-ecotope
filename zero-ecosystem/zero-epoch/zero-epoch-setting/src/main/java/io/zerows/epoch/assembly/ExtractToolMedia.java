package io.zerows.epoch.assembly;

import io.zerows.support.Ut;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Media resolver
 * 1. consumes ( default = application/json )
 * 2. produces ( default = application/json )
 */
class ExtractToolMedia {

    private static final Set<MediaType> DEFAULTS = new HashSet<MediaType>() {
        {
            this.add(MediaType.WILDCARD_TYPE);
        }
    };

    /**
     * Capture the consume mime types
     *
     * @param method method reference
     *
     * @return return MIME
     */
    public static Set<MediaType> consumes(final Method method) {
        return resolve(method, Consumes.class);
    }

    /**
     * Capture the produces mime types
     *
     * @param method method reference
     *
     * @return return MIME
     */
    public static Set<MediaType> produces(final Method method) {
        return resolve(method, Produces.class);
    }

    private static Set<MediaType> resolve(final Method method,
                                          final Class<? extends Annotation>
                                              mediaCls) {
        final Annotation anno = method.getAnnotation(mediaCls);
        if (Objects.isNull(anno)) {
            return DEFAULTS;
        }
        final String[] value = Ut.invoke(anno, "get");
        final Set<MediaType> result = new HashSet<>();
        // RxJava 2
        Arrays.stream(value)
            .filter(Objects::nonNull)
            .map(MediaType::valueOf)
            .filter(Objects::nonNull)
            .forEach(result::add);
        return result.isEmpty() ? DEFAULTS : result;
    }
}
