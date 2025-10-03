package io.zerows.epoch.component.scan;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.annotations.Ordered;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.component.log.Annal;
import io.zerows.constant.VValue;
import io.zerows.epoch.corpus.exception._40052Exception500FilterSpecification;
import io.zerows.epoch.corpus.exception._40053Exception500FilterOrder;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.uca.Inquirer;
import io.zerows.epoch.sdk.modeling.web.Filter;

import javax.servlet.annotation.WebFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Filter processing
 * path = Event Chain
 */
public class InquirerFilter implements Inquirer<ConcurrentMap<String, Set<Event>>> {

    private static final Annal LOGGER = Annal.get(InquirerFilter.class);

    @Override
    public ConcurrentMap<String, Set<Event>> scan(final Set<Class<?>> clazzes) {
        // Scan all classess that are annotated with @WebFilter
        final ConcurrentMap<String, Set<Event>> filters = new ConcurrentHashMap<>();
        Observable.fromIterable(clazzes)
            .filter(item -> item.isAnnotationPresent(WebFilter.class))
            .map(this::ensure)
            .subscribe(item -> this.extract(filters, item))
            .dispose();
        return filters;
    }

    private Class<?> ensure(final Class<?> clazz) {
        Fn.jvmKo(!Filter.class.isAssignableFrom(clazz), _40052Exception500FilterSpecification.class, clazz);
        return clazz;
    }

    private void extract(final ConcurrentMap<String, Set<Event>> map,
                         final Class<?> clazz) {
        final Annotation annotation = clazz.getAnnotation(WebFilter.class);
        final String[] pathes = Ut.invoke(annotation, "value");
        // Multi pathes supported
        for (final String path : pathes) {
            final Event event = this.extract(path, clazz);
            // Set<Event> initialized.
            Set<Event> events = map.get(path);
            if (null == events) {
                events = new HashSet<>();
            }
            // Add new event to set
            events.add(event);
            map.put(path, events);
        }
    }

    private Event extract(final String path, final Class<?> clazz) {
        final Event event = new Event();
        event.setPath(path);
        final Annotation annotation = clazz.getAnnotation(Ordered.class);
        int order = KWeb.ORDER.FILTER;
        if (null != annotation) {
            final Integer setted = Ut.invoke(annotation, "value");
            // Order specification
            Fn.jvmKo(setted < 0, _40053Exception500FilterOrder.class, clazz);
            order = order + setted;
        }
        event.setOrder(order);
        event.setProxy(clazz);
        // Action
        final Method action = this.findMethod(clazz);
        event.setAction(action);
        event.setConsumes(new HashSet<>());
        event.setProduces(new HashSet<>());
        return event;
    }

    private Method findMethod(final Class<?> clazz) {
        final List<Method> methods = new ArrayList<>();
        // One method only
        final Method[] scanned = clazz.getDeclaredMethods();
        Observable.fromArray(scanned)
            .filter(item -> "doFilter".equals(item.getName()))
            .subscribe(methods::add)
            .dispose();
        // No overwritting
        if (VValue.ONE == methods.size()) {
            return methods.get(VValue.IDX);
        } else {
            // Search for correct signature
            return Observable.fromIterable(methods)
                .filter(this::isValidFilter)
                .blockingFirst();
        }
    }

    private boolean isValidFilter(final Method method) {
        final Class<?>[] parameters = method.getParameterTypes();
        boolean valid = false;
        if (VValue.TWO == parameters.length) {
            final Class<?> requestCls = parameters[VValue.IDX];
            final Class<?> responseCls = parameters[VValue.ONE];
            if (HttpServerRequest.class == requestCls && HttpServerResponse.class == responseCls) {
                valid = true;
            }
        }
        return valid;
    }
}
