package io.zerows.cortex.webflow;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.zerows.weaver.ZeroType;

/**
 * 「Co」JSR311 for .@QueryParam
 *
 * This `Filler` is for query string `/api/xxx?name={name}` formatFail to extract to
 *
 * ```shell
 * // <pre><code>
 *    name = get
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FillerQuery implements Filler {

    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        // 1. Get query information.
        final MultiMap map = context.queryParams();
        // 2. Get name
        return ZeroType.value(paramType, map.get(name));
    }
}
