package io.zerows.epoch.corpus.io.uca.request.mime.parse;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KWeb;
import io.zerows.component.log.Annal;
import io.zerows.epoch.component.serialization.ZeroType;
import io.zerows.constant.VValue;
import io.zerows.epoch.corpus.io.uca.request.argument.Filler;
import io.zerows.epoch.corpus.model.Epsilon;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.program.Ut;
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
public class EpsilonIncome implements Income<List<Epsilon<Object>>> {

    private static final Annal LOGGER = Annal.get(EpsilonIncome.class);
    private static final Cc<String, Atomic<Object>> CC_ATOMIC = Cc.openThread();

    @Override
    public List<Epsilon<Object>> in(final RoutingContext context,
                                    final Event event)
        throws WebException {
        final Method method = event.getAction();
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] annoTypes = method.getParameterAnnotations();
        final List<Epsilon<Object>> args = new ArrayList<>();
        for (int idx = 0; idx < paramTypes.length; idx++) {

            /* For each field specification **/
            final Epsilon<Object> epsilon = new Epsilon<>();
            epsilon.setArgType(paramTypes[idx]);
            epsilon.setAnnotation(this.getAnnotation(annoTypes[idx]));
            epsilon.setName(this.getName(epsilon.getAnnotation()));

            /* Default Value **/
            epsilon.setDefaultValue(this.getDefault(annoTypes[idx], epsilon.getArgType()));

            /* Epsilon income -> outcome **/
            final Atomic<Object> atomic = CC_ATOMIC.pick(MimeAtomic::new); // FnZero.po?lThread(POOL_ATOMIC, MimeAtomic::new);
            final Epsilon<Object> outcome = atomic.ingest(context, epsilon);
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
