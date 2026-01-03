package io.zerows.plugins.security.htdigest;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.htdigest.HtdigestAuth;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import io.zerows.plugins.security.metadata.YmSecuritySpec;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-30
 */
@SPID("Security/HT_DIGEST")
@Slf4j
public class SecurityProviderHtDigest implements SecurityProvider {
    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config) {
        throw new _501NotSupportException("[ PLUG ] Security/HT-DIGEST 不支持 401 Handler 构造");
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        log.info("🔐 / 启用HT-Digest：{}", options.encode());


        final String filename = Ut.valueString(options, YmSecuritySpec.htdigest.options.filename);
        return CC_PROVIDER_401.pick(
                () -> HtdigestAuth.create(vertxRef, filename),
                filename
        );
    }
}
