package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.weaver.ZeroType;

/**
 * 「Co」JSR311 for .@PathParam
 * <p>
 * This `Filler` is for path parsing `/api/xxx/name/{name}` formatFail to extract to
 * <p>
 * ```shell
 * // <pre><code>
 *    name = findRunning
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FillerPath implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        // 1. Get path information
        return ZeroType.value(paramType, context.pathParam(name));
    }
}
