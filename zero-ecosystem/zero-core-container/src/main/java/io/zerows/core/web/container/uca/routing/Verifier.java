package io.zerows.core.web.container.uca.routing;

import io.zerows.core.fn.RFn;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.container.exception.BootAnnotationRepeatException;
import io.zerows.core.web.container.exception.BootEventActionNoneException;
import io.zerows.core.web.container.exception.BootParamAnnotationException;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.core.web.io.annotations.StreamParam;
import io.zerows.core.web.io.uca.request.argument.Filler;
import io.zerows.core.web.model.atom.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Verifier {

    private static final Annal LOGGER = Annal.get(Verifier.class);

    @SuppressWarnings("all")
    public static void verify(final Event event) {
        final Method method = event.getAction();
        RFn.outBoot(null == method, LOGGER, BootEventActionNoneException.class,
            Verifier.class, event);
        /* Specification **/
        verify(method, BodyParam.class);
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

        RFn.outBoot(1 < occurs, LOGGER, BootAnnotationRepeatException.class,
            Verifier.class, method.getName(), annoCls, occurs);
    }

    public static void verify(final Parameter parameter) {
        final Annotation[] annotations = parameter.getDeclaredAnnotations();
        final List<Annotation> annotationList = Arrays.stream(annotations)
            .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
            .toList();

        final int multi = annotationList.size();
        RFn.outBoot(1 < multi, LOGGER, BootParamAnnotationException.class,
            Verifier.class, parameter.getName(), multi);
    }
}
