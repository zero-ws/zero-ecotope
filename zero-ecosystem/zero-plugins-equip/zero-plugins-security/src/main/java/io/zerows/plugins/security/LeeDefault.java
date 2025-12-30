package io.zerows.plugins.security;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.Lee;

/**
 * @author lang : 2025-10-29
 */
public class LeeDefault implements Lee {

    @Override
    public String encode(final JsonObject payload, final SecurityType type) {
        return this.reference(type).encode(payload, type);
    }

    @Override
    public JsonObject decode(final String token, final SecurityType type) {
        return this.reference(type).decode(token, type);
    }

    private Lee reference(final SecurityType type) {
        if (SecurityType.BASIC == type) {
            return Lee.of(LeeBasic::new);
        }
        return Lee.of();
    }
}
