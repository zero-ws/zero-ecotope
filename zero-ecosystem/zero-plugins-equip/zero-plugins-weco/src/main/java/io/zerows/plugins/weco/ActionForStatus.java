package io.zerows.plugins.weco;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.vertx.common.cache.AkkaOr;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.WeCoUtil;

import java.time.Duration;

@SPID(WeCoAction.ACTION_CHAT_STATUS)
public class ActionForStatus implements WeCoAction<String> {
    @Override
    public Akka<UniResponse> executeAsync(final UniMessage<String> request) {
        // 读取 expireSeconds
        final int expireSeconds = WeCoUtil.inputExpired(request);
        // Payload 约定为 UUID 字符串
        final String uuid = request.payload();

        if (uuid == null) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: UUID");
        }

        // 1. 构建缓存 Key 并查询 SPI 存储
        final String sessionKey = WeCoSession.keyOf(uuid);
        final Duration storeDuration = Duration.ofSeconds(expireSeconds);
        final WeCoAsyncSession session = WeCoAsyncSession.of();
        return AkkaOr.of(session.getAsync(sessionKey, storeDuration)
            .map(WeCoUtil::replyStatus)
            .map(UniResponse::success)
        );
    }
}
