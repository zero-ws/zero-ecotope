package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;

/**
 * 「Co」Critical Specific
 *
 * This `Filler` is placeholder for Body/Stream data in RESTful web request here
 *
 * In current Filler, it do nothing because the body extracting will go through
 * `Resolver` for different MIME here.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FillerEmpty implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        return null;
    }
}
