package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.support.Ut;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.ContextParam;
import jakarta.ws.rs.extension.PointParam;
import jakarta.ws.rs.extension.SessionParam;
import jakarta.ws.rs.extension.StreamParam;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 「Co」Zero for JSR311 Arguments
 *
 * There are a uniform request flow to get parameters to support JSR311 in zero framework, here I defined **Filler**
 * interface ( Fill argument into container, it's specific component )
 *
 * 1. In common request workflow, it provide standard `key = get` attributes into map.
 * 2. For body/stream request workflow, there should be placeholder named `EmptyFiller` to taken the flow node and then continue for extracting.
 *
 * For Standard JSR311, it support most parameter annotations and extend JSR311 for income requirement.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Filler {
    Cc<String, Filler> CC_FILLTER = Cc.openThread();
    /**
     * JSR311 Standard mapping from annotation to `Filler` component
     */
    ConcurrentMap<Class<? extends Annotation>, Supplier<Filler>> PARAMS =
        new ConcurrentHashMap<>() {
            {
                // JSR311 Provided
                this.put(QueryParam.class, supplier(FillerQuery.class));
                this.put(FormParam.class, supplier(FillerForm.class));
                this.put(PathParam.class, supplier(FillerPath.class));
                this.put(HeaderParam.class, supplier(FillerHeader.class));
                this.put(CookieParam.class, supplier(FillerCookie.class));

                // Extension
                this.put(BodyParam.class, supplier(FillerEmpty.class));
                this.put(StreamParam.class, supplier(FillerEmpty.class));

                this.put(SessionParam.class, supplier(FillerSession.class));
                this.put(ContextParam.class, supplier(FillerContext.class));
                this.put(PointParam.class, supplier(FillerPoint.class));
            }
        };
    /**
     * The type of parameters that will use `EmptyFiller` for taking place.
     */
    Set<Class<? extends Annotation>> NO_VALUE =
        new HashSet<Class<? extends Annotation>>() {
            {
                this.add(BodyParam.class);
                this.add(StreamParam.class);
            }
        };

    static Supplier<Filler> supplier(final Class<?> clazz) {
        return () -> CC_FILLTER.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    /**
     * The major code logic to get the get of input field name here.
     *
     * @param name      The parameter name
     * @param paramType The parameter declared type
     * @param datum     The `RoutingContext` of Vert.x ( vertx-web )
     *
     * @return The extracted get of parameter
     */
    Object apply(String name,
                 Class<?> paramType,
                 RoutingContext datum);
}
