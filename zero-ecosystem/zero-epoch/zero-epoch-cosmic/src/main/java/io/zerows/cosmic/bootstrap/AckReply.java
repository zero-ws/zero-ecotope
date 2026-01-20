package io.zerows.cosmic.bootstrap;

import io.r2mo.spi.SPI;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.webflow.Wings;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VString;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-10-13
 */
@Slf4j
class AckReply implements Ack {

    private final RoutingContext context;

    AckReply(final RoutingContext context) {
        this.context = context;
    }

    @Override
    public void handle(final Envelop envelop, final HttpServerResponse response,
                       final Set<MediaType> mediaTypes) {
        /* 📤 响应处理 */
        if (response.headWritten()) {
            // ❌️ 响应头已发送，直接跳出
            response.closed();
            return;
        }


        /*
         * 📋 设置当前请求/事件的响应数据
         * MIME类型在此步骤之前已设置（mime ( HttpServerResponse, Set<MediaType> )
         */
        if (response.ended()) {
            // ❌️ 响应体已结束，直接跳出
            response.closed();
            return;
        }


        /*
         * 📅 设置日期头部，参考RESTful Cookbook
         * 此头部表示发生时间（错误/成功）
         */
        response.putHeader(HttpHeaders.DATE, Instant.now().toString());
        if (HttpMethod.HEAD == envelop.method()) {
            /*
             * 🔍 是否为头部方法
             * 当是头部方法时，在特殊情况下处理头部请求
             * 仅头部
             * 1. @HEAD 注解
             * 2. 无数据响应（无内容）
             */
            final WebState state = SPI.V_STATUS.ok204();
            response.setStatusCode(state.state());
            response.setStatusMessage(state.name());
            response.end(VString.EMPTY);
            return;
        }


        /* ✅️ 标准流程 */
        final String headerStr = response.headers().get(HttpHeaders.CONTENT_TYPE);
        final Wings wings = this.handler(headerStr, mediaTypes);
        wings.output(response, envelop);
        response.closed();
    }

    private Wings handler(final String contentType, final Set<MediaType> produces) {
        /*
         * 📄 内容类型
         * ✅ 接受
         */
        final MediaType type;
        if (Objects.isNull(contentType)) {
            /*
             * 🏃‍♂️ 默认字符串模式
             *
             * 1. 🏷️ Content-Type 为 `* / *` 格式失败
             * 2. 📤 直接回复主体
             */
            type = MediaType.WILDCARD_TYPE;
        } else {
            /*
             * 📋 从响应头部提取数据 `MediaType`
             */
            type = MediaType.valueOf(contentType);
        }
        /*
         * 1. 📌 type 为第一级
         * 2. 📊 subtype 为第二级
         */
        final Vertx vertxRef = this.context.vertx();
        final ConcurrentMap<String, Function<Vertx, Wings>> subtype = Wings.SELECT_POOL.get(type.getType());
        final Wings selected;
        if (Objects.isNull(subtype) || subtype.isEmpty()) {
            selected = Wings.SELECT_POOL
                .get(MediaType.APPLICATION_JSON_TYPE.getType())
                .get(MediaType.APPLICATION_JSON_TYPE.getSubtype())
                .apply(vertxRef);
        } else {
            final Function<Vertx, Wings> wings = subtype.get(type.getSubtype());
            selected = Objects.isNull(wings) ?
                Wings.DEFAULT.apply(this.context.vertx()) :
                wings.apply(vertxRef);
        }
        log.debug("[ ZERO ] Wings 响应选择 `{}` 用于内容类型 {}, MIME = {}, hashCode = {}",
            selected.getClass().getName(), contentType, type, selected.hashCode());
        return selected;
    }
}
