package io.zerows.cosmic.bootstrap;

import io.r2mo.function.Fn;
import io.zerows.cortex.webflow.Filler;
import io.zerows.cosmic.exception._40008Exception500EventActionNone;
import io.zerows.cosmic.exception._40029Exception500AnnotationRepeat;
import io.zerows.cosmic.exception._40030Exception500ParamAnnotation;
import io.zerows.epoch.web.WebEvent;
import io.zerows.support.Ut;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.StreamParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AxisVerifier {

    @SuppressWarnings("all")
    public static void verify(final WebEvent event) {
        final Method method = event.getAction();
        Fn.jvmKo(Objects.isNull(method), _40008Exception500EventActionNone.class, event);
        /* Specification **/
        verify(method, BodyParam.class);
        verify(method, BeanParam.class);
        verify(method, StreamParam.class);
        /* Field Specification **/
        for (final Parameter parameter : method.getParameters()) {
            verify(parameter);
        }
    }

    public static void verify(final Method method, final Class<? extends Annotation> annoCls) {
        final Annotation[][] annotations = method.getParameterAnnotations();
        final AtomicInteger integer = new AtomicInteger(0);
        Ut.itMatrix(annotations, (annotation) -> {
            if (annotation.annotationType() == annoCls) {
                integer.incrementAndGet();
            }
        });
        final int occurs = integer.get();

        Fn.jvmKo(1 < occurs, _40029Exception500AnnotationRepeat.class, method, annoCls, occurs);
    }

    public static void verify(final Parameter parameter) {
        final Annotation[] annotations = parameter.getDeclaredAnnotations();
        final List<Annotation> annotationList = Arrays.stream(annotations)
            .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
            .toList();

        final int multi = annotationList.size();
        Fn.jvmKo(1 < multi, _40030Exception500ParamAnnotation.class, parameter.getName(), multi);
    }
}
