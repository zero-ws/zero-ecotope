package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.zerows.cortex.management.uri.UriMeta;

import java.util.List;

/**
 * The tunnel for Uris
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ScRoutine {
    /*
     * Search `UriMeta` definition by channel
     *
     * Permission / Resource management
     */
    Future<List<UriMeta>> searchAsync(String keyword, String sigma);
}
