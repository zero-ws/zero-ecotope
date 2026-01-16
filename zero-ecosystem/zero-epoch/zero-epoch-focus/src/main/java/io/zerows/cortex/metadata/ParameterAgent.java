package io.zerows.cortex.metadata;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
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

    static ParameterBuilder<RoutingContext> of() {
        return CCT_PARAM.pick(ParameterAgent::new);
    }

    /**
     * 支持类型表
     * <pre>
     *    - {@link XHeader}
     *    - {@link Session}
     *    - {@link HttpServerRequest}
     *    - {@link HttpServerResponse}
     *    - {@link Vertx}
     *    - {@link EventBus}
     *    - {@link User}
     *    - {@link Set} (FileUpload)
     *    - {@link JsonArray}   / 有值
     *    - {@link JsonObject}  / 有值
     *    - {@link Buffer}      / 有值
     *    - {@link FileUpload}  / 有值
     * </pre>
     *
     * @param context   路由上下文
     * @param type      参数类型
     * @param extension 扩展参数（Agent中不包含）
     * @return 参数对象
     */
    @Override
    public Object build(final RoutingContext context, final Class<?> type, final Object... extension) {
        Object returnValue = null;
        if (ParameterPre.is(type, XHeader.class)) {
            final HttpServerRequest request = context.request();
            final MultiMap headers = request.headers();
            final XHeader header = new XHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else if (ParameterPre.is(type, Session.class)) {
            returnValue = context.session();
        } else if (ParameterPre.is(type, HttpServerRequest.class)) {
            returnValue = context.request();
        } else if (ParameterPre.is(type, HttpServerResponse.class)) {
            returnValue = context.response();
        } else if (ParameterPre.is(type, Vertx.class)) {
            returnValue = context.vertx();
        } else if (ParameterPre.is(type, EventBus.class)) {
            returnValue = context.vertx().eventBus();
        } else if (ParameterPre.is(type, User.class)) {
            returnValue = context.user();
        } else if (ParameterPre.is(type, UserAt.class)) {
            returnValue = context.get(UserAt.class.getName());
        } else if (ParameterPre.is(type, Set.class)) {
            returnValue = new HashSet<>(context.fileUploads());
        } else if (ParameterPre.is(type, JsonArray.class)) {
            returnValue = context.body().asJsonArray();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonArray();
            }
        } else if (ParameterPre.is(type, JsonObject.class)) {
            returnValue = context.body().asJsonObject();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonObject();
            }
        } else if (ParameterPre.is(type, Buffer.class)) {
            returnValue = context.body().buffer();
            if (Objects.isNull(returnValue)) {
                returnValue = Buffer.buffer();
            }
        } else if (ParameterPre.is(type, FileUpload.class)) {
            final Set<FileUpload> uploads = new HashSet<>(context.fileUploads());
            if (!uploads.isEmpty()) {
                returnValue = uploads.iterator().next();
            }
        }
        return returnValue;
    }
}
