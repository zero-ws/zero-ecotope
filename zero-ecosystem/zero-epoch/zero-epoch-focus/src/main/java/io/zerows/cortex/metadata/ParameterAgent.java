package io.zerows.cortex.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
class ParameterAgent implements ParameterBuilder<RoutingContext> {
    private static final Cc<String, ParameterBuilder<RoutingContext>> CCT_PARAM = Cc.openThread();

    private ParameterAgent() {
    }

    private static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || Ut.isImplement(paramType, expected);
    }

    static ParameterBuilder<RoutingContext> of() {
        return CCT_PARAM.pick(ParameterAgent::new);
    }

    @Override
    public Object build(final RoutingContext context, final Class<?> type) {
        Object returnValue = null;
        if (is(type, XHeader.class)) {
            /*
             * XHeader for
             * - sigma
             * - id
             * - appKey
             * - lang
             */
            final HttpServerRequest request = context.request();
            final MultiMap headers = request.headers();
            final XHeader header = new XHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else if (is(type, Session.class)) {
            /* Http Session */
            returnValue = context.session();
        } else if (is(type, HttpServerRequest.class)) {
            /* HttpServerRequest type */
            returnValue = context.request();
        } else if (is(type, HttpServerResponse.class)) {
            /* HttpServerResponse type */
            returnValue = context.response();
        } else if (is(type, io.vertx.core.Vertx.class)) {
            /* Vertx type */
            returnValue = context.vertx();
        } else if (is(type, EventBus.class)) {
            /* EventBus type */
            returnValue = context.vertx().eventBus();
        } else if (is(type, User.class)) {
            /* User type */
            returnValue = context.user();
        } else if (is(type, Set.class)) {
            /*
             * It's only for file uploading here.
             * ( FileUpload ) type here for actual in agent
             */
            returnValue = new HashSet<>(context.fileUploads());
        } else if (is(type, JsonArray.class)) {
            /*
             * JsonArray, Could get from Serialization
             */
            returnValue = context.body().asJsonArray();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonArray();
            }
        } else if (is(type, JsonObject.class)) {
            /*
             * JsonObject, Could get from Serialization
             */
            returnValue = context.body().asJsonObject();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonObject();
            }
        } else if (is(type, Buffer.class)) {
            /*
             * Buffer, Could get from Serialization
             */
            returnValue = context.body().buffer();
            if (Objects.isNull(returnValue)) {
                returnValue = Buffer.buffer();
            }
        } else if (is(type, FileUpload.class)) {
            /*
             * Single FileUpload
             */
            final Set<FileUpload> uploads = new HashSet<>(context.fileUploads());
            if (!uploads.isEmpty()) {
                returnValue = uploads.iterator().next();
            }
        }
        return returnValue;
    }
}
