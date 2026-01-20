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
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.web.Token;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.support.Ut;

import java.util.Objects;

public class JwtToken implements Token {
    // 防止 JWT 的高频解码（速度很慢）
    private static final Cc<String, JsonObject> STORE_TOKEN = Cc.open();
    private static final Cc<String, JwtToken> CC_TOKEN = Cc.openThread();
    private static JWTAuth PROVIDER;
    private final JWTAuthOptions options;

    public JwtToken() {
        final SecurityConfig config = SecurityActor.configJwt();
        if (Objects.isNull(config)) {
            throw new _501NotSupportException("[ PLUG ] Jwt 配置不存在，无法执行 JWT 操作！");
        }
        final JsonObject options = config.options();
        if (Ut.isNil(options)) {
            throw new _501NotSupportException("[ PLUG ] Jwt 配置项数据为空 {}，无法执行 JWT 操作！");
        }
        this.options = new JWTAuthOptions(options);
    }

    public static JwtToken of() {
        return CC_TOKEN.pick(JwtToken::new);
    }

    private JWTAuth provider() {
        if (Objects.isNull(PROVIDER)) {
            final Vertx vertx = StoreVertx.of().vertx();
            PROVIDER = JWTAuth.create(vertx, this.options);
        }
        return PROVIDER;
    }

    @Override
    public String encode(final JsonObject payload) {
        return Objects.requireNonNull(this.provider()).generateToken(payload, this.options.getJWTOptions());
    }

    @Override
    public JsonObject decode(final String token) {
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
