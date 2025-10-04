package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.cosmic.exception._60052Exception411ContentLength;
import io.zerows.cosmic.plugins.validation.Rigor;
import io.zerows.cosmic.plugins.validation.ValidatorEntry;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.assembly.DiProxyInstance;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.Kv;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

class AimFlower {

    public static final String RIGOR_NOT_FOUND = "Zero system could not find Rigor for type = {0}.";
    private static final Annal LOGGER = Annal.get(AimFlower.class);

    static <T> Future<Envelop> next(final RoutingContext context,
                                    final T entity) {
        final Envelop envelop = Envelop
            .success(entity)
            .bind(context);     // Bind Data Here.
        /*
         * Extension System of:
         * 1) PlugAuditor
         * 2) PlugRegion
         */
        return Ambit.of(ActionNext.class).then(context, envelop);
    }

    static void replyError(final RoutingContext context,
                           final WebException error,
                           final WebEvent event) {
        final Envelop envelop = Envelop.failure(error);
        AimAnswer.reply(context, envelop, event);
    }

    static void executeRequest(final RoutingContext context,
                               final Map<String, List<WebRule>> rulers,
                               final WebRequest wrapRequest,
                               final Object[] args,
                               final ValidatorEntry verifier) {
        // Extract major object
        WebException error = verifyPureArguments(verifier, wrapRequest, args);
        if (null == error) {
            error = verifyUpload(context);
        }
        if (null == error) {
            // Check if annotated with @Codex
            final Kv<Integer, Class<?>> found = findParameter(wrapRequest.getEvent().getAction());
            if (null == found.value()) {
                // Verify here.
                context.next();
            } else {
                // @Codex validation for different types
                final Class<?> type = found.value();
                final Object value = args[found.key()];
                verifyCodex(context, rulers, wrapRequest, type, value);
            }
        } else {
            // Hibernate validate handler
            replyError(context, error, wrapRequest.getEvent());
        }
    }

    private static Kv<Integer, Class<?>> findParameter(
        final Method method) {
        int index = 0;
        final Kv<Integer, Class<?>> result = Kv.create();
        for (final Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Codex.class)) {
                result.set(index, parameter.getType());
                break;
            }
            index++;
        }
        return result;
    }

    private static void verifyCodex(final RoutingContext context,
                                    final Map<String, List<WebRule>> rulers,
                                    final WebRequest wrapRequest,
                                    final Class<?> type,
                                    final Object value) {
        final Rigor rigor = Rigor.get(type);
        if (null == rigor) {
            LOGGER.warn(RIGOR_NOT_FOUND, type);
            context.next();
        } else {
            final WebException error = rigor.verify(rulers, value);
            if (null == error) {
                // Ignore Errors
                context.next();
            } else {
                // Reply Error
                replyError(context, error, wrapRequest.getEvent());
            }
        }
    }

    private static WebException verifyPureArguments(
        final ValidatorEntry verifier,
        final WebRequest wrapRequest,
        final Object[] args) {
        final WebEvent event = wrapRequest.getEvent();
        final Object proxy = event.getProxy();
        final Method method = event.getAction();
        WebException error = null;
        try {
            final Object delegate;
            if (proxy instanceof final DiProxyInstance proxyInstance) {
                // Validation for dynamic proxy
                delegate = proxyInstance.proxy();
            } else {
                // Validation for proxy
                delegate = proxy;
            }
            verifier.verifyMethod(delegate, method, args);
        } catch (final WebException ex) {
            // Basic validation handler
            error = ex;
        }
        return error;
    }

    private static WebException verifyUpload(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        if (request.isExpectMultipart()) {
            if (!request.headers().contains(HttpHeaders.CONTENT_LENGTH)) {
                /*
                 * Content-Length = 0
                 */
                return new _60052Exception411ContentLength(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
