package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.webflow.Later;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VString;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Response process to normalize the response request.
 * 1. Media definition
 * 2. Operation based join event, envelop, configure
 */
@Slf4j
public final class AckFlow {

    public static Envelop previous(final RoutingContext context) {
        Envelop envelop = context.get(KWeb.ARGS.REQUEST_BODY);
        if (Objects.isNull(envelop)) {
            envelop = Envelop.failure(new _500ServerInternalException("[ R2MO ] 之前出现的错误：" + KWeb.ARGS.REQUEST_BODY));
        }
        return envelop;
    }

    public static void next(final RoutingContext context, final Envelop envelop) {
        if (envelop.valid()) {
            /*
             * Next step here
             */
            context.put(KWeb.ARGS.REQUEST_BODY, envelop);
            context.next();
        } else {
            reply(context, envelop);
        }
    }

    public static void normalize(final RoutingContext context, final Envelop envelop) {
        if (envelop.valid()) {
            /*
             * Updated here
             */
            envelop.bind(context);
            context.put(KWeb.ARGS.REQUEST_BODY, envelop);
            context.next();
        } else {
            reply(context, envelop);
        }
    }

    public static void reply(final RoutingContext context, final Envelop envelop) {
        reply(context, envelop, new HashSet<>());
    }

    public static void reply(final RoutingContext context, final Envelop envelop, final Supplier<Set<MediaType>> supplier) {
        Set<MediaType> produces = Objects.isNull(supplier) ? new HashSet<>() : supplier.get();
        if (Objects.isNull(produces)) {
            produces = new HashSet<>();
        }
        reply(context, envelop, produces);
    }

    public static void reply(final RoutingContext context, final Envelop envelop, final WebEvent event) {
        Set<MediaType> produces;
        if (Objects.isNull(event)) {
            produces = new HashSet<>();
        } else {
            produces = event.getProduces();
            if (Objects.isNull(produces)) {
                produces = new HashSet<>();
            }
        }
        reply(context, envelop, produces, Objects.isNull(event) ? null : event.getAction());
    }

    private static void reply(final RoutingContext context, final Envelop envelop, final Set<MediaType> mediaTypes) {
        reply(context, envelop, mediaTypes, null);
    }

    private static void reply(final RoutingContext context, final Envelop envelop,
                              final Set<MediaType> mediaTypes, final Method sessionAction) {
        final HttpServerResponse response = context.response();
        /*
         * FIX: java.lang.IllegalStateException: Response is closed
         * 只有响应没有发送的时候才继续执行
         */
        if (response.closed()) {
            // ❌️ 响应已关闭，直接中断
            return;
        }


        /*
         * 📤 在响应上设置HTTP状态信息，所有信息来自`Envelop`
         * 1) 🏷️ 状态码
         * 2) 📝 状态消息
         */
        final WebState code = envelop.status();
        response.setStatusCode(code.state());
        response.setStatusMessage(code.name());



        /*
         * 📎 绑定数据
         */
        envelop.bind(context);
        /*
         * 📱 MIME 处理
         */
        replyMedia(response, mediaTypes);



        /*
         * 🛡️ 响应安全设置，异常响应直接中断返回
         */
        if (!envelop.valid()) {
            // ❌️ 出现异常，直接中断
            reply(context, envelop);
            return;
        }


        replySecurity(response);

        final Object data = envelop.data();
        final Later<Object> laterSession = Later.ofSession(context);
        laterSession.execute(data, sessionAction);
        /*
         * 💉 响应回复的注入扩展（插件）
         */
        Ambit.of(AmbitReply.class).then(context, envelop).compose(processed -> {
            /*
             * 📤 当前情况的输出，
             * 此处已被DataRegion替换。
             * 🐛 修复BUG：在旧工作流中，下面的代码不在`OAmbit`的compose中，异步会影响这里的响应数据，可能导致
             * 响应保持原始状态，并且ACL工作流无法正常处理响应数据序列化。
             */
            Ack.of(context).handle(processed, response, mediaTypes);



            /*
             * ------------------------ 此处是异步广播流程，不操作响应 ------------------------
             * 🆕 新功能：将数据发布到@Off地址
             */
            final Later<Envelop> later = Later.ofNotify(context);
            later.execute(processed, sessionAction);


            return Future.succeededFuture();
        }).otherwise(error -> {
            log.error(error.getMessage(), error);
            return null;
        });
    }

    private static void replyMedia(final HttpServerResponse response, final Set<MediaType> produces) {
        /*
         * 📤 响应头已发送，直接跳出
         */
        if (response.headWritten()) {
            // ❌️ 中断
            return;
        }


        /*
         * 🏷️ @Produces 表示服务器生成响应给客户端
         */
        if (produces.isEmpty()) {
            // ❌️ 当前API未设置 `produces`，选择默认 `application/json`。
            response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return;
        }


        if (produces.contains(MediaType.WILDCARD_TYPE)) {
            // ❌️ 这里设置了 `.* / .*` 通配符类型，选择默认 `application/json`。
            response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return;
        }


        /*
         * 📋 从集合中提取媒体类型
         */
        final MediaType type = produces.iterator().next();
        if (Objects.isNull(type)) {
            /*
             * 📄 未设置内容类型，默认情况，选择默认 `application/json`
             */
            response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        } else {
            /*
             * 🔗 类型 + 斜杠 + 子类型
             */
            final String content = type.getType() + VString.SLASH + type.getSubtype();
            response.putHeader(HttpHeaders.CONTENT_TYPE, content);
        }
    }

    private static void replySecurity(final HttpServerResponse response) {
        /*
         * 📤 响应头已发送，直接跳出
         */
        if (response.headWritten()) {
            // ❌️ 中断
            return;
        }


        /* 📚 参考: https://vertx.io/blog/writing-secure-vert-x-web-apps/ */
        response
            /*
             * 🚫 不允许代理缓存数据
             */
            .putHeader(HttpHeaders.CACHE_CONTROL, "no-get, no-cache")
            /*
             * 🛡️ 防止Internet Explorer从MIME嗅探
             * 响应偏离声明的内容类型
             */
            .putHeader("X-Content-Type-Options", "nosniff")
            /*
             * 🔒 严格HTTPS（约6个月）
             */
            .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
            /*
             * 📎 IE8+ 不允许在此资源上下文中打开附件
             */
            .putHeader("X-Download-Options", "noopen")
            /*
             * ✨ 为IE启用XSS保护
             */
            .putHeader("X-XSS-Protection", "1; mode=block")
            /*
             * 📼 拒绝框架嵌入
             */
            .putHeader("X-FRAME-OPTIONS", "DENY");
    }
}
