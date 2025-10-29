package io.zerows.plugins.security.authenticate;

import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AdapterCommon extends AbstractAdapter {
    @Override
    public AuthenticationProvider provider(final SecurityMeta aegis) {
        //        // Chain Provider
        //        final ChainAuth chain = ChainAuth.all();
        //        final AuthenticationProvider provider = this.provider401Internal(aegis);
        //        if (Objects.nonNull(provider)) {
        //            chain.add(provider);
        //        }
        //        // 2. Wall Provider ( Based join Annotation )
        //        final AuthenticationProvider wallProvider = AuthenticateBuiltInProvider.provider(aegis);
        //        chain.add(wallProvider);
        //        return chain;
        return null;
    }
}
