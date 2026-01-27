package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.WebEvent;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import io.zerows.weaver.ZeroType;
import jakarta.ws.rs.DefaultValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Help to extract epsilon
 */
public class IncomeEpsilon implements Income<List<WebEpsilon<Object>>> {

    private static final Cc<String, Atomic<Object>> CC_ATOMIC = Cc.openThread();

    @Override
    public List<WebEpsilon<Object>> in(final RoutingContext context,
                                       final WebEvent event)
        throws WebException {
        final Method method = event.getAction();
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] annoTypes = method.getParameterAnnotations();
        final List<WebEpsilon<Object>> args = new ArrayList<>();
        for (int idx = 0; idx < paramTypes.length; idx++) {

            /* For each field specification **/
            final WebEpsilon<Object> epsilon = new WebEpsilon<>();
            epsilon.setArgType(paramTypes[idx]);
            epsilon.setAnnotation(this.getAnnotation(annoTypes[idx]));
            epsilon.setName(this.getName(epsilon.getAnnotation()));

            /* Default Value **/
            epsilon.setDefaultValue(this.getDefault(annoTypes[idx], epsilon.getArgType()));

            /* Epsilon income -> outcome **/
            final Atomic<Object> atomic = CC_ATOMIC.pick(AtomicMime::new); // FnZero.po?lThread(POOL_ATOMIC, MimeAtomic::new);
            final WebEpsilon<Object> outcome = atomic.ingest(context, epsilon);
            args.add(outcome);
        }
        return args;
    }

    @SuppressWarnings("all")
    private String getName(final Annotation annotation) {
        if (Objects.isNull(annotation)) {
            return KWeb.ARGS.MIME_IGNORE;
        }
        if (Filler.NO_VALUE.contains(annotation.annotationType())) {
            return KWeb.ARGS.MIME_DIRECT;
        }
        return Ut.invoke(annotation, "value");
    }

    private Annotation getAnnotation(final Annotation[] annotations) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
            .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
            .toList();
        return annotationList.isEmpty() ? null : annotationList.get(VValue.IDX);
    }

    private Object getDefault(final Annotation[] annotations,
                              final Class<?> paramType) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
            .filter(item -> item.annotationType() == DefaultValue.class)
            .toList();
        if (annotationList.isEmpty()) {
            return null;
        }
        final Annotation annotation = annotationList.get(VValue.IDX);
        return ZeroType.value(paramType,
            Ut.invoke(annotation, "value"));
    }
}
