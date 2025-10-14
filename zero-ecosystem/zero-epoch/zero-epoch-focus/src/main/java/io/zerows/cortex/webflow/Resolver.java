package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

/**
 * # 「Co」Zero Resolver
 * The interface that zero provide for request content resolving for
 * 1. Data Format Conversation
 * 2. Default Value Setting
 *
 * @param <T> generic type
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Resolver<T> {
    /**
     * Critical: You should call `setValue` in your code logical or you'll get null get
     *
     * @param context Vertx-web RoutingContext reference
     * @param income  Zero definition of {@link WebEpsilon} class
     *
     * @return The same type of {@link WebEpsilon} class
     * @throws WebException When some error occurs, throw WebException out
     */
    WebEpsilon<T> resolve(RoutingContext context,
                          WebEpsilon<T> income);

    /**
     * # 「Co」Zero Solve
     * This component will be called by specific `Resolver` internally.
     *
     * @param <T> Generic class
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface Solve<T> {
        /**
         * Resolving workflow in `Resolver`
         *
         * @param content The body content of Class formatFail
         *
         * @return The deserialization generic pojo class here
         * @throws WebException exception of Web request
         */
        T resolve(String content);
    }
}
