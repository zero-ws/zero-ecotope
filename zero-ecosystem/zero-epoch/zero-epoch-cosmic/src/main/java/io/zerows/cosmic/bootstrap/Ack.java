package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import jakarta.ws.rs.core.MediaType;

import java.util.Objects;
import java.util.Set;

/**
 * 响应处理器
 *
 * @author lang : 2025-10-13
 */
public interface Ack {

    Cc<String, Ack> CC_SKELETON = Cc.openThread();

    static Ack of(final RoutingContext context) {
        final String cacheKey = Objects.requireNonNull(context.vertx()).hashCode() + "@" + AckReply.class.getName();
        return CC_SKELETON.pick(() -> new AckReply(context), cacheKey);
    }

    void handle(Envelop envelop, HttpServerResponse response, Set<MediaType> mediaTypes);
}
