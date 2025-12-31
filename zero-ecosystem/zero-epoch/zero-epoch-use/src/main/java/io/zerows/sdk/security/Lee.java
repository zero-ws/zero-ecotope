package io.zerows.sdk.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;
import io.zerows.spi.HPI;

import java.util.function.Supplier;

/**
 * Encode/Decode Coder
 * 编码/解码的核心组件（安全专用接口）
 *
 * @author lang : 2025-10-29
 */
public interface Lee {
    Cc<String, Lee> CC_LEE = Cc.openThread();

    static Lee of(final Supplier<Lee> constructorFn) {
        return CC_LEE.pick(constructorFn, String.valueOf(constructorFn));
    }

    static Lee of() {
        return CC_LEE.pick(() -> HPI.findOneOf(Lee.class), Lee.class.getName());
    }

    String encode(JsonObject payload, SecurityType type);

    JsonObject decode(String token, SecurityType type);
}
