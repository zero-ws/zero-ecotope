package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.annotations.Ordered;
import io.zerows.epoch.assembly.exception._40052Exception500FilterSpecification;
import io.zerows.epoch.assembly.exception._40053Exception500FilterOrder;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Filter;
import io.zerows.platform.constant.VValue;
import jakarta.servlet.annotation.WebFilter;

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
public class InquirerForFilter implements Inquirer<ConcurrentMap<String, Set<WebEvent>>> {

    @Override
    public ConcurrentMap<String, Set<WebEvent>> scan(final Set<Class<?>> clazzes) {
        // Scan all classess that are annotated with @WebFilter
        final ConcurrentMap<String, Set<WebEvent>> filters = new ConcurrentHashMap<>();
        for (final Class<?> clazz : clazzes) {
            if (clazz.isAnnotationPresent(WebFilter.class)) {
                Fn.jvmKo(!Filter.class.isAssignableFrom(clazz), _40052Exception500FilterSpecification.class, clazz);
                this.extract(filters, clazz);
            }
        }
        return filters;
    }

    private void extract(final ConcurrentMap<String, Set<WebEvent>> map,
                         final Class<?> clazz) {
        final WebFilter annotation = clazz.getAnnotation(WebFilter.class);
        final String[] pathes = annotation.value();
        // Multi pathes supported
        for (final String path : pathes) {
            final WebEvent event = this.extract(path, clazz);
            // Set<Event> initialized.
            Set<WebEvent> events = map.get(path);
            if (null == events) {
                events = new HashSet<>();
            }
            // Add new event to set
            events.add(event);
            map.put(path, events);
        }
    }

    private WebEvent extract(final String path, final Class<?> clazz) {
        final WebEvent event = new WebEvent();
        event.setPath(path);
        final Ordered annotation = clazz.getAnnotation(Ordered.class);
        int order = KWeb.ORDER.FILTER;
        if (null != annotation) {
            final int setted = annotation.value();
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
        // Scan for all valid methods defined in Filter interface
        final Method[] scanned = clazz.getDeclaredMethods();
        for (final Method method : scanned) {
            if (Filter.METHODS.contains(method.getName()) && this.isValidFilter(method)) {
                methods.add(method);
            }
        }

        // At least one method must exist
        if (methods.isEmpty()) {
            throw new _40052Exception500FilterSpecification(clazz);
        }

        try {
            // Return doFilter as executable action
            return clazz.getMethod(Filter.METHOD_FILTER, HttpServerRequest.class, HttpServerResponse.class);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
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
