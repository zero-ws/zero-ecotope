package io.zerows.sdk.security;

import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;

import java.util.Objects;

/**
 * {@link Token} 调用的静态工具类，可根据 {@link SecurityType} 类型创建不同的 Token 实例进行编解码动作，一般编
 * 解码动作只有二选一：Basic / JWT，此处的静态类可以帮忙完成所有的中转执行工作，而 JWT 则是未来的重点支持方向。
 *
 * @author lang : 2025-10-29
 */
class TokenUtil {

    static String encode(final JsonObject payload, final SecurityType type) {
        return ofLee().encode(payload, type);
    }

    static JsonObject decode(final String token, final SecurityType type) {
        return ofLee().decode(token, type);
    }

    private static Lee ofLee() {
        final Lee lee = SPI.findOverwrite(Lee.class);
        if (Objects.isNull(lee)) {
            throw new _501NotSupportException("[ ZERO ] 为找到 Lee 编解码实现，无法执行操作！");
        }
        return lee;
    }
}
