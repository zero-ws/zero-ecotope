package io.zerows.core.web.io.zdk.mime;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.atom.Epsilon;

/**
 * # 「Co」Zero Resolver
 *
 * The interface that zero provide for request content resolving for
 *
 * 1. Data Format Conversation
 * 2. Default Value Setting
 *
 * @param <T> generic type
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Resolver<T> {
    /**
     * Critical: You should call `setValue` in your code logical or you'll get null value
     *
     * @param context Vertx-web RoutingContext reference
     * @param income  Zero definition of {@link Epsilon} class
     *
     * @return The same type of {@link Epsilon} class
     * @throws WebException When some error occurs, throw WebException out
     */
    Epsilon<T> resolve(RoutingContext context,
                       Epsilon<T> income);
}
