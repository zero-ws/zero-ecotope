package io.zerows.core.web.io.uca.request.argument;

import io.vertx.ext.web.RoutingContext;
import io.zerows.module.domain.uca.serialization.ZeroType;

/**
 * 「Co」JSR311 for .@PathParam
 *
 * This `Filler` is for path parsing `/api/xxx/name/{name}` formatFail to extract to
 *
 * ```shell
 * // <pre><code>
 *    name = value
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class PathFiller implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        // 1. Get path information
        return ZeroType.value(paramType, context.pathParam(name));
    }
}
