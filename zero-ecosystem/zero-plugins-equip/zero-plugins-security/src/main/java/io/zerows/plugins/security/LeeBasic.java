package io.zerows.plugins.security;

import io.r2mo.function.Fn;
import io.r2mo.jce.common.HED;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.security.exception._40079Exception500SecurityType;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.Lee;
import io.zerows.support.Ut;

/**
 * @author lang : 2025-10-29
 */
class LeeBasic implements Lee {
    @Override
    public String encode(final JsonObject payload, final SecurityType type) {
        Fn.jvmKo(SecurityType.BASIC != type,
            _40079Exception500SecurityType.class, SecurityType.BASIC, type);
        final String username = Ut.valueString(payload, KName.USERNAME);
        final String password = Ut.valueString(payload, KName.PASSWORD);
        if (username != null) {
            // RFC check
            if (username.indexOf(':') != -1) {
                throw new IllegalArgumentException("[ ZERO ] 用户名中不可以包含 ':'");
            }
        }
        final String builder = username + ":" + password;
        return HED.encodeBase64(builder);
    }

    @Override
    public JsonObject decode(final String token, final SecurityType type) {
        Fn.jvmKo(SecurityType.BASIC != type,
            _40079Exception500SecurityType.class, SecurityType.BASIC, type);
        final String[] content = HED.decodeBase64(token).split(":");
        return new JsonObject()
            .put(KName.USERNAME, content[0])
            .put(KName.PASSWORD, content[1]);
    }
}
