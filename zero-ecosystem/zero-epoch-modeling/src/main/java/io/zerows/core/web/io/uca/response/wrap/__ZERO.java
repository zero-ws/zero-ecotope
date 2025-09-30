package io.zerows.core.web.io.uca.response.wrap;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
interface CACHE {

    @Memory(Wings.class)
    Cc<String, Wings> CC_WINGS = Cc.openThread();

    ConcurrentMap<String, ConcurrentMap<String, Supplier<Wings>>> SELECT_POOL = new ConcurrentHashMap<>() {
        {
            /* Type `*` */
            this.put(MediaType.WILDCARD_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    /* SubType `*` */
                    this.put(MediaType.WILDCARD_TYPE.getSubtype(),
                        () -> CACHE.CC_WINGS.pick(JsonWings::new, MediaType.WILDCARD_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, JsonWings::new, MediaType.WILDCARD_TYPE.toString())
                    );
                }
            });

            /* Type `application` */

            this.put(MediaType.APPLICATION_JSON_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    /* SubType: json */
                    this.put(MediaType.APPLICATION_JSON_TYPE.getSubtype(),
                        () -> CACHE.CC_WINGS.pick(JsonWings::new, MediaType.APPLICATION_JSON_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, JsonWings::new, MediaType.APPLICATION_JSON_TYPE.toString())
                    );

                    /* SubType: octet-stream */
                    this.put(MediaType.APPLICATION_OCTET_STREAM_TYPE.getSubtype(),
                        () -> CACHE.CC_WINGS.pick(BufferWings::new, MediaType.APPLICATION_OCTET_STREAM_TYPE.toString())
                        // () -> FnZero.po?lThread(POOL_THREAD, BufferWings::new, MediaType.APPLICATION_OCTET_STREAM_TYPE.toString()));
                    );
                }
            });
        }
    };
}
