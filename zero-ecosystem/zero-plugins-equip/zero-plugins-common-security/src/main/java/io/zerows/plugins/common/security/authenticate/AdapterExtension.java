package io.zerows.plugins.common.security.authenticate;

import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.metadata.security.Aegis;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AdapterExtension extends AbstractAdapter {

    private final AuthenticationProvider standard;

    AdapterExtension(final AuthenticationProvider standard) {
        this.standard = standard;
    }

    @Override
    public AuthenticationProvider provider(final Aegis aegis) {
        final AuthenticateBuiltInProvider provider = AuthenticateBuiltInProvider.provider(aegis);
        return credentials ->
            AdapterExtension.this.standard.authenticate(credentials)
                .compose(nil -> provider.authenticate(credentials));
    }
}
