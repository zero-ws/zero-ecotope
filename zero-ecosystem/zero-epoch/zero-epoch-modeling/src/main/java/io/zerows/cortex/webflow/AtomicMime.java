package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.application.YmlCore;
import io.zerows.management.OZeroStore;
import io.zerows.platform.enums.EmMime;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * # 「Co」Zero Mime Processing here
 *
 * This component will process the request data before your code occurs
 *
 * @param <T> generic definition
 */
@SuppressWarnings("all")
public class AtomicMime<T> implements Atomic<T> {
    public static final String RESOLVER = "( Resolver ) Select resolver {0} " +
        "for Content-Type {1} when request to {2}";
    public static final String RESOLVER_DEFAULT = "( Resolver ) Select resolver {0} as [DEFAULT] " +
        "for Content-Type = null when request to {1}";
    public static final String RESOLVER_CONFIG = "( Resolver ) Select resolver from " +
        "annotation config \"{0}\" for Content-Type {1}";
    private static final Annal LOGGER = Annal.get(AtomicMime.class);

    private static final ConcurrentMap<String, Atomic> POOL_ATOMIC = new ConcurrentHashMap<>();
    private static final Cc<String, Atomic> CC_ATOMIC = Cc.openThread();
    private static final Cc<String, Resolver> CC_RESOLVER = Cc.openThread();
    private static final Cc<String, Solve> CC_SOLVE = Cc.openThread();

    @Override
    public WebEpsilon<T> ingest(final RoutingContext context,
                                final WebEpsilon<T> income)
        throws WebException {
        final WebEpsilon<T> epsilon;
        if (EmMime.Flow.TYPED == income.getMime()) {
            /* Resolver **/
            final Atomic<T> atomic = CC_ATOMIC.pick(AtomicTyped::new, AtomicTyped.class.getName());
            // FnZero.po?lThread(POOL_ATOMIC, TypedAtomic::new, TypedAtomic.class.getName());
            epsilon = atomic.ingest(context, income);
        } else if (EmMime.Flow.STANDARD == income.getMime()) {
            /* System standard filler **/
            final Atomic<T> atomic = CC_ATOMIC.pick(AtomicStandard::new, AtomicStandard.class.getName());
            // FnZero.po?lThread(POOL_ATOMIC, StandardAtomic::new, StandardAtomic.class.getName());
            epsilon = atomic.ingest(context, income);
        } else {
            /* Resolver **/
            final Resolver<T> resolver = this.getResolver(context, income);
            epsilon = resolver.resolve(context, income);
        }
        return epsilon;
    }

    private Resolver<T> getResolver(final RoutingContext context,
                                    final WebEpsilon<T> income) {
        /* 1.Read the resolver first **/
        final Annotation annotation = income.getAnnotation();
        final Class<?> resolverCls = Ut.invoke(annotation, YmlCore.resolver.__KEY);
        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        /* 2.Check configured in default **/
        if (ResolverUnset.class == resolverCls) {
            /* 3. Old path **/
            final JsonObject content = OZeroStore.option(YmlCore.resolver.__KEY);
            final String resolver;
            if (null == header) {
                resolver = content.getString("default");
                LOGGER.info(RESOLVER_DEFAULT, resolver, context.request().absoluteURI());
            } else {
                final MediaType type = MediaType.valueOf(header);
                final JsonObject resolverMap = content.getJsonObject(type.getType());
                resolver = resolverMap.getString(type.getSubtype());
                LOGGER.info(RESOLVER, resolver, header, context.request().absoluteURI());
            }
            return CC_RESOLVER.pick(() -> Ut.instance(resolver), resolver);
            // FnZero.po?lThread(POOL_RESOLVER, () -> Ut.instance(resolver), resolver);
        } else {
            LOGGER.info(RESOLVER_CONFIG, resolverCls, header);
            /*
             * Split workflow
             * Resolver or Solve
             */
            if (Ut.isImplement(resolverCls, Resolver.class)) {
                /*
                 * Resolver Directly
                 */
                return CC_RESOLVER.pick(() -> Ut.instance(resolverCls), resolverCls.getName());
                // FnZero.po?lThread(POOL_RESOLVER, () -> Ut.instance(resolverCls), resolverCls.getName());
            } else {
                /*
                 * Solve component, contract to set Solve<Tool> here.
                 */
                final Resolver<T> resolver = CC_RESOLVER.pick(() -> Ut.instance(SolveResolver.class), SolveResolver.class.getName());
                // FnZero.po?lThread(POOL_RESOLVER, () -> Ut.instance(SolveResolver.class), SolveResolver.class.getName());
                final Solve solve = CC_SOLVE.pick(() -> Ut.instance(resolverCls), resolverCls.getName());
                // FnZero.po?lThread(POOL_SOLVE, () -> Ut.instance(resolverCls), resolverCls.getName());
                Ut.contract(resolver, Solve.class, solve);
                return resolver;
            }
        }
    }
}
