package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.annotations.meta.Memory;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public interface Wings {
    Function<Vertx, Wings> DEFAULT = WingsJson::new;
    @Memory(Wings.class)
    Cc<String, Wings> CC_WINGS = Cc.openThread();
    ConcurrentMap<String, ConcurrentMap<String, Function<Vertx, Wings>>> SELECT_POOL = new ConcurrentHashMap<>() {
        {
            // *
            this.put(MediaType.WILDCARD_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    // */*
                    this.put(MediaType.WILDCARD_TYPE.getSubtype(),
                        (vertx) -> CC_WINGS.pick(() -> DEFAULT.apply(vertx), MediaType.WILDCARD_TYPE.toString())
                    );
                }
            });

            // application
            this.put(MediaType.APPLICATION_JSON_TYPE.getType(), new ConcurrentHashMap<>() {
                {
                    // application/json
                    this.put(MediaType.APPLICATION_JSON_TYPE.getSubtype(),
                        (vertx) -> CC_WINGS.pick(() -> DEFAULT.apply(vertx), MediaType.APPLICATION_JSON_TYPE.toString())
                    );

                    // application/octet-stream
                    this.put(MediaType.APPLICATION_OCTET_STREAM_TYPE.getSubtype(),
                        (vertx) -> CC_WINGS.pick(() -> new WingsBuffer(vertx), MediaType.APPLICATION_OCTET_STREAM_TYPE.toString())
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
