package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.assembly.exception._40005Exception500EventSource;
import io.zerows.epoch.assembly.exception._40036Exception500CodexMore;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.support.Ut;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scanned @EndPoint clazz to build Event metadata
 */
@Slf4j
public class ExtractorEvent implements Extractor<Set<WebEvent>> {

    @Override
    public Set<WebEvent> extract(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return new HashSet<>();
        }
        // 1. Class verify
        this.verify(clazz);
        // 2. Check whether clazz annotated with @PATH
        final Set<WebEvent> result = new HashSet<>();
        if (clazz.isAnnotationPresent(Path.class)) {
            // 3.1. Append Root Path
            final Path path = this.path(clazz);
            assert null != path : "Path should not be null.";
            result.addAll(this.extract(clazz, ExtractToolPath.resolve(path)));
        } else {
            // 3.2. Use method Path directly
            result.addAll(this.extract(clazz, null));
        }
        return result;
    }

    private void verify(final Class<?> clazz) {
        // Check basic specification: No Arg Constructor
        if (!clazz.isInterface()) {
            // Class direct.
            ExtractToolVerifier.noArg(clazz);
        }
        ExtractToolVerifier.modifier(clazz);
        // Event Source Checking
        if (!clazz.isAnnotationPresent(EndPoint.class)) {
            throw new _40005Exception500EventSource(clazz);
        }
    }

    @SuppressWarnings("all")
    private Set<WebEvent> extract(final Class<?> clazz, final String root) {
        final Set<WebEvent> events = new HashSet<>();
        // 0.Preparing
        final Method[] methods = clazz.getDeclaredMethods();
        // 1.Validate Codex annotation appears
        final Long counter = Observable.fromArray(methods)
            .map(Method::getParameterAnnotations)
            .flatMap(Observable::fromArray)
            .map(Arrays::asList)
            .map(item -> item.stream().map(Annotation::annotationType).collect(Collectors.toList()))
            .filter(item -> item.contains(Codex.class))
            .count().blockingGet();
        Fn.jvmKo(methods.length < counter, _40036Exception500CodexMore.class, clazz);
        // 2.Build Set
        events.addAll(Arrays.stream(methods).filter(ExtractToolMethod::isValid)
            .map(item -> this.extract(item, root))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
        // 3.Break the Event `priority` draw down.
        return events;
    }

    /**
     * Scan for single
     *
     * @param method single method that will be scanned.
     * @param root   root path calculation
     *
     * @return Standard Event object
     */
    private WebEvent extract(final Method method, final String root) {
        // 1.Method path
        final WebEvent event = new WebEvent();
        // 2.Method resolve
        final HttpMethod httpMethod = ExtractToolMethod.resolve(method);
        if (null == httpMethod) {
            // Ignored the method could not be annotated.
            log.warn("\u001b[0;31m!!!!!, Missed HttpMethod annotation for method\u001b[m ? (GET,POST,PUT,...). method = \u001b[0;31m{}\u001b[m", method);
            return null;
        } else {
            event.setMethod(httpMethod);
        }
        {
            // 3.1. Get path from method
            final Path path = this.path(method);
            if (null == path) {
                // 3.2. Check root double check
                if (!Ut.isNil(root)) {
                    // Use root directly.
                    event.setPath(root);
                }
            } else {
                final String result = ExtractToolPath.resolve(
                    path, root);
                event.setPath(result);
            }
        }
        // 4.Action
        event.setAction(method);
        // 6.Mime resolve
        event.setConsumes(ExtractToolMedia.consumes(method));
        event.setProduces(ExtractToolMedia.produces(method));
        // 7. Instance clazz for proxy
        final Class<?> clazz = method.getDeclaringClass();
        event.setProxy(clazz);
        // 8. Order
        if (method.isAnnotationPresent(Adjust.class)) {
            final Adjust adjust = method.getDeclaredAnnotation(Adjust.class);
            final int order = adjust.value();
            event.setOrder(order);
        }
        return event;
    }


    private Path path(final Class<?> clazz) {
        return this.path(clazz.getDeclaredAnnotation(Path.class));
    }

    private Path path(final Method method) {
        return this.path(method.getDeclaredAnnotation(Path.class));
    }

    private Path path(final Annotation anno) {
        return (anno instanceof Path) ? (Path) anno : null;
    }
}
