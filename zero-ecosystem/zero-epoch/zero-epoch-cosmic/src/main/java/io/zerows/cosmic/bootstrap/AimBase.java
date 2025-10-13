package io.zerows.cosmic.bootstrap;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.cortex.InvokerUtil;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.cortex.webflow.Analyzer;
import io.zerows.cortex.webflow.AnalyzerMedia;
import io.zerows.cosmic.exception._60002Exception500DeliveryError;
import io.zerows.cosmic.exception._60003Exception500EntityCast;
import io.zerows.cosmic.plugins.validation.ValidatorEntry;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base class to provide template method
 */
public abstract class AimBase {
    private static final Cc<String, Analyzer> CC_ANALYZER = Cc.openThread();
    private static final Cc<String, ValidatorEntry> CC_VALIDATOR = Cc.openThread();

    private transient final Analyzer analyzer = CC_ANALYZER.pick(AnalyzerMedia::new, AnalyzerMedia.class.getName());
    // FnZero.po?lThread(POOL_ANALYZER, MediaAnalyzer::new, MediaAnalyzer.class.getName());
    private transient final ValidatorEntry verifier = CC_VALIDATOR.pick(ValidatorEntry::new);
    // FnZero.po?lThread(POOL_VALIDATOR, Validator::new);

    /**
     * Template method
     *
     * @param context RoutingContext reference
     * @param event   Event object of definition
     *
     * @return TypedArgument ( Object[] )
     */
    protected Object[] buildArgs(final RoutingContext context,
                                 final WebEvent event) {
        Object[] cached = context.get(KWeb.ARGS.REQUEST_CACHED);
        if (null == cached) {
            cached = this.analyzer.in(context, event);
            context.put(KWeb.ARGS.REQUEST_CACHED, cached);
        }
        // Validation handler has been getNull the parameters.
        return cached;
    }


    /**
     * Get event bus address.
     *
     * @param event Event object of definition
     *
     * @return Get event bus address
     */
    protected String address(final WebEvent event) {
        final Method method = event.getAction();
        final Annotation annotation = method.getDeclaredAnnotation(Address.class);
        return Ut.invoke(annotation, "get");
    }

    /**
     * @param event Event object of definition
     * @param args  TypedArgument ( Object[] )
     *
     * @return Return invoked result
     */
    protected Object invoke(final WebEvent event, final Object[] args) {
        final Method method = event.getAction();
        this.logger().info("[ ZERO ] Class = {}, Method = {}, Args = {}",
            method.getName(), Ut.fromJoin(args), method.getDeclaringClass().getName());
        return InvokerUtil.invoke(event.getProxy(), method, args);
    }

    protected Envelop failure(final String address,
                              final AsyncResult<Message<Envelop>> handler) {
        final Throwable cause = handler.cause();
        final WebException error;
        if (Objects.isNull(cause)) {
            error = new _60002Exception500DeliveryError(address, "Unknown");
        } else {
            if (cause instanceof WebException) {
                error = (WebException) cause;
            } else {
                error = new _60002Exception500DeliveryError(address, "Jvm: " + cause.getMessage());
            }
        }
        return Envelop.failure(error);
    }

    protected Envelop success(final String address,
                              final AsyncResult<Message<Envelop>> handler
    ) {
        Envelop envelop;
        try {
            final Message<Envelop> message = handler.result();
            envelop = message.body();
        } catch (final Throwable ex) {
            final WebException error
                = new _60003Exception500EntityCast(address, ex.getMessage());
            envelop = Envelop.failure(error);
        }
        return envelop;
    }

    protected ValidatorEntry verifier() {
        return this.verifier;
    }

    protected Logger logger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected void executeRequest(final RoutingContext context,
                                  final Map<String, List<WebRule>> rulers,
                                  final WebRequest wrapRequest) {
        try {
            final Object[] args = this.buildArgs(context, wrapRequest.getEvent());
            // Execute web flow and uniform call.
            AimFlower.executeRequest(context, rulers, wrapRequest, args, this.verifier());
        } catch (final WebException error) {
            // Bad request of 400 for parameter processing
            AimFlower.replyError(context, error, wrapRequest.getEvent());
        } catch (final Exception ex) {
            // DEBUG:
            ex.printStackTrace();
        }
    }

    protected void exec(final Actuator consumer,
                        final RoutingContext context,
                        final WebEvent event) {
        try {
            // Monitor
            this.logger().debug("[ ZERO ] Web flow 执行器: {}", event.getAction());
            {
                final Session session = context.session();
                if (Objects.nonNull(session)) {
                    // Fix: 3.9.1 cookie error of null dot
                    final Cookie cookie = context.request().getCookie(VValue.DEFAULT_SESSION);
                    this.logger().debug("[ ZERO ] ( Session ) Path = {}, Session Id = {}, Cookie {}",
                        context.request().path(),
                        session.id(), Objects.isNull(cookie) ? null : cookie.getValue());
                }
            }
            Fn.jvmAt(consumer);
        } catch (final WebException ex) {
            AimFlower.replyError(context, ex, event);
        }
    }
}
