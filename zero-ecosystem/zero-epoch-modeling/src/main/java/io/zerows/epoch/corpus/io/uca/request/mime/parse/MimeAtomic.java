package io.zerows.epoch.corpus.io.uca.request.mime.parse;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.corpus.io.uca.response.resolver.SolveResolver;
import io.zerows.epoch.corpus.io.uca.response.resolver.UnsetResolver;
import io.zerows.epoch.corpus.io.zdk.mime.Resolver;
import io.zerows.epoch.corpus.io.zdk.mime.Solve;
import io.zerows.epoch.corpus.model.Epsilon;
import io.zerows.epoch.enums.EmMime;
import io.zerows.epoch.mem.OZeroStore;
import io.zerows.epoch.program.Ut;
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
public class MimeAtomic<T> implements Atomic<T> {
    private static final Annal LOGGER = Annal.get(MimeAtomic.class);

    private static final ConcurrentMap<String, Atomic> POOL_ATOMIC = new ConcurrentHashMap<>();
    private static final Cc<String, Atomic> CC_ATOMIC = Cc.openThread();
    private static final Cc<String, Resolver> CC_RESOLVER = Cc.openThread();
    private static final Cc<String, Solve> CC_SOLVE = Cc.openThread();

    @Override
    public Epsilon<T> ingest(final RoutingContext context,
                             final Epsilon<T> income)
        throws WebException {
        final Epsilon<T> epsilon;
        if (EmMime.Flow.TYPED == income.getMime()) {
            /* Resolver **/
            final Atomic<T> atomic = CC_ATOMIC.pick(TypedAtomic::new, TypedAtomic.class.getName());
            // FnZero.po?lThread(POOL_ATOMIC, TypedAtomic::new, TypedAtomic.class.getName());
            epsilon = atomic.ingest(context, income);
        } else if (EmMime.Flow.STANDARD == income.getMime()) {
            /* System standard filler **/
            final Atomic<T> atomic = CC_ATOMIC.pick(StandardAtomic::new, StandardAtomic.class.getName());
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
                                    final Epsilon<T> income) {
        /* 1.Read the resolver first **/
        final Annotation annotation = income.getAnnotation();
        final Class<?> resolverCls = Ut.invoke(annotation, YmlCore.resolver.__KEY);
        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        /* 2.Check configured in default **/
        if (UnsetResolver.class == resolverCls) {
            /* 3. Old path **/
            final JsonObject content = OZeroStore.option(YmlCore.resolver.__KEY);
            final String resolver;
            if (null == header) {
                resolver = content.getString("default");
                LOGGER.info(INFO.RESOLVER_DEFAULT, resolver, context.request().absoluteURI());
            } else {
                final MediaType type = MediaType.valueOf(header);
                final JsonObject resolverMap = content.getJsonObject(type.getType());
                resolver = resolverMap.getString(type.getSubtype());
                LOGGER.info(INFO.RESOLVER, resolver, header, context.request().absoluteURI());
            }
            return CC_RESOLVER.pick(() -> Ut.instance(resolver), resolver);
            // FnZero.po?lThread(POOL_RESOLVER, () -> Ut.instance(resolver), resolver);
        } else {
            LOGGER.info(INFO.RESOLVER_CONFIG, resolverCls, header);
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
