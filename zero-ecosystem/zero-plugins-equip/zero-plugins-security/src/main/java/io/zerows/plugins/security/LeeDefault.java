package io.zerows.plugins.security;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.Lee;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-10-29
 */
public class LeeDefault implements Lee {

    @Override
    public String encode(final JsonObject payload, final SecurityType type) {
        return Lee.of(CC_SUPPLIER.get(type)).encode(payload, type);
    }

    @Override
    public JsonObject decode(final String token, final SecurityType type) {
        return Lee.of(CC_SUPPLIER.get(type)).decode(token, type);
    }

    private static final ConcurrentMap<SecurityType, Supplier<Lee>> CC_SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(SecurityType.JWT, LeeJwt::new);
            this.put(SecurityType.BASIC, LeeBasic::new);
        }
    };
}
