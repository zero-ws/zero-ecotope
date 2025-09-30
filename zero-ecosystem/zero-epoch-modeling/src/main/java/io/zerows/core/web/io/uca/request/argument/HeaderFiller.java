package io.zerows.core.web.io.uca.request.argument;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.zerows.module.domain.uca.serialization.ZeroType;

/**
 * 「Co」JSR311 for .@HeaderParam
 *
 * This `Filler` is for header map `key=value` extract such as
 *
 * ```shell
 * // <pre><code>
 *    Content-Type = application/json
 *    Authorization = Basic xxxxx
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HeaderFiller implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        // Extract request from header
        final HttpServerRequest request = context.request();
        return ZeroType.value(paramType, request.getHeader(name));
    }
}
