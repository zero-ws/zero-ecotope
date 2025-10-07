package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.annotations.meta.Memory;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 「Co」Zero Framework
 *
 * Here I added new configuration `freedom` to zero framework as critical data specification here for
 * old system integration here. This configuration is new released after `0.5.3`
 *
 * ```yaml
 * // <pre><code>
 * zero:
 *   freedom: true
 * // </code></pre>
 * ```
 *
 * * The default get of `freedom` is false, it means that you must be under zero data specification.
 * * You Also can use your own setting to set `freedom` to true, it means original raw data.
 *
 * Here are two features
 *
 * 1. Build response by `Accept` and `Content-Type`, set the media type
 * 2. Convert media type to actual response data.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Wings {
    @Memory(Wings.class)
    Cc<String, Wings> CC_WINGS = Cc.openThread();
    ConcurrentMap<String, ConcurrentMap<String, Supplier<Wings>>> SELECT_POOL = new ConcurrentHashMap<>() {
        {
            /* Type `*` */
            this.put(MediaType.WILDCARD_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    /* SubType `*` */
                    this.put(MediaType.WILDCARD_TYPE.getSubtype(),
                        () -> CC_WINGS.pick(WingsJson::new, MediaType.WILDCARD_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, JsonWings::new, MediaType.WILDCARD_TYPE.toString())
                    );
                }
            });

            /* Type `application` */

            this.put(MediaType.APPLICATION_JSON_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    /* SubType: json */
                    this.put(MediaType.APPLICATION_JSON_TYPE.getSubtype(),
                        () -> CC_WINGS.pick(WingsJson::new, MediaType.APPLICATION_JSON_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, JsonWings::new, MediaType.APPLICATION_JSON_TYPE.toString())
                    );

                    /* SubType: octet-stream */
                    this.put(MediaType.APPLICATION_OCTET_STREAM_TYPE.getSubtype(),
                        () -> CC_WINGS.pick(WingsBuffer::new, MediaType.APPLICATION_OCTET_STREAM_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, BufferWings::new, MediaType.APPLICATION_OCTET_STREAM_TYPE.toString()));
                    );
                }
            });
        }
    };

    /**
     * Pre-Condition
     * 1) Response is not ended
     * 2) The request method is not HEAD
     *
     * @param response ServerResponse reference
     * @param envelop  The response uniform model
     */
    void output(HttpServerResponse response, Envelop envelop);
}
