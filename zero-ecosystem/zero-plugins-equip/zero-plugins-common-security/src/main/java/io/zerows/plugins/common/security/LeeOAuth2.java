package io.zerows.plugins.common.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.epoch.corpus.security.AegisItem;
import io.zerows.epoch.program.Ut;
import io.zerows.plugins.common.security.authenticate.AdapterProvider;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class LeeOAuth2 extends AbstractLee {
    private static final Cc<String, OAuth2Auth> CC_PROVIDER = Cc.openThread();

    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final Aegis config) {
        // Options
        final OAuth2Auth provider = this.providerInternal(vertx, config);
        final String callback = this.option(config, YmlCore.secure.oauth2.options.CALLBACK);
        final OAuth2AuthHandler standard;
        if (Ut.isNil(callback)) {
            standard = OAuth2AuthHandler.create(vertx, provider);
        } else {
            standard = OAuth2AuthHandler.create(vertx, provider, callback);
        }
        return this.wrapHandler(standard, config);
    }

    @Override
    public AuthenticationProvider provider(final Vertx vertx, final Aegis config) {
        final OAuth2Auth standard = this.providerInternal(vertx, config);
        final AdapterProvider extension = AdapterProvider.extension(standard);
        return extension.provider(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Auth providerInternal(final Vertx vertx, final Aegis config) {
        // Options
        final AegisItem item = config.item();
        final OAuth2Options options = new OAuth2Options(item.options());
        final String key = item.wall().name() + options.hashCode();
        return CC_PROVIDER.pick(() -> OAuth2Auth.create(vertx, options), key);
        // return Fn.po?lThread(POOL_PROVIDER, () -> OAuth2Auth.create(vertx, options), key);
    }

    @Override
    public String encode(final JsonObject data, final AegisItem config) {
        return null;
    }

    @Override
    public JsonObject decode(final String token, final AegisItem config) {
        return null;
    }
}
