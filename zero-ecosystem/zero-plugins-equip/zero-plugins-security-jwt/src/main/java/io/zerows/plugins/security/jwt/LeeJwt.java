package io.zerows.plugins.security.jwt;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.impl.jose.JWT;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cosmic.plugins.security.exception._40079Exception500SecurityType;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.sdk.security.Lee;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 默认编解码处理器，主要用于 JWT 的编解码，此处的配置来源于启动过程中的配置注入：
 * <pre>
 *     vertx:
 *       security:
 *         jwt:
 *           options: {@link JsonObject} - JWT 配置项
 * </pre>
 *
 * @author lang : 2025-10-29
 */
public class LeeJwt implements Lee {
    // 防止 JWT 的高频解码（速度很慢）
    private static final Cc<String, JsonObject> STORE_TOKEN = Cc.open();
    private static JWTAuth PROVIDER;
    private final JWTAuthOptions options;

    public LeeJwt() {
        final SecurityConfig CONFIG = SecurityActor.configJwt();
        if (Objects.isNull(CONFIG)) {
            throw new _501NotSupportException("[ PLUG ] Jwt 配置不存在，无法执行 JWT 操作！");
        }
        final JsonObject options = CONFIG.options();
        if (Ut.isNil(options)) {
            throw new _501NotSupportException("[ PLUG ] Jwt 配置项数据为空 {}，无法执行 JWT 操作！");
        }
        this.options = new JWTAuthOptions(options);
    }

    private JWTAuth provider() {
        if (Objects.isNull(PROVIDER)) {
            final Vertx vertx = StoreVertx.of().vertx();
            PROVIDER = JWTAuth.create(vertx, this.options);
        }
        return PROVIDER;
    }

    @Override
    public String encode(final JsonObject payload, final SecurityType type) {
        Fn.jvmKo(SecurityType.JWT != type,
            _40079Exception500SecurityType.class, SecurityType.JWT, type);
        return Objects.requireNonNull(this.provider()).generateToken(payload);
    }

    @Override
    public JsonObject decode(final String token, final SecurityType type) {
        Fn.jvmKo(SecurityType.JWT != type,
            _40079Exception500SecurityType.class, SecurityType.JWT, type);
        if (Objects.isNull(token)) {
            return new JsonObject();
        }
        return STORE_TOKEN.pick(() -> this.decodeToken(token), token);
    }

    // 防止 JWT 的高频解码（速度很慢）
    private JsonObject decodeToken(final String token) {
        // 危险的反射操作，未来版本若有 Vert.x 中可用的 API 可换掉
        final JWT jwt = Ut.field(this.provider(), "jwt");
        return Objects.isNull(jwt) ? new JsonObject() :
            Fn.jvmOr(() -> jwt.decode(token));
    }
}
