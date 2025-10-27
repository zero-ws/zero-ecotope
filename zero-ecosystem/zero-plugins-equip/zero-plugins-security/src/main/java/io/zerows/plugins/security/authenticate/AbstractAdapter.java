package io.zerows.plugins.security.authenticate;

import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.platform.enums.EmSecure;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractAdapter implements AdapterProvider {
    private static final AtomicBoolean LOG_401 = new AtomicBoolean(Boolean.TRUE);

    protected AuthenticationProvider provider401Internal(final KSecurity aegis) {
        final KSecurity.Provider item = aegis.item();
        final Class<?> providerCls = item.getProviderAuthenticate();
        if (Objects.isNull(providerCls)) {
            return null;
        }
        final EmSecure.SecurityType wall = aegis.getType();
        final AuthenticationProvider provider = Ut.invokeStatic(providerCls, YmlCore.secure.PROVIDER, aegis);
        if (Objects.isNull(provider)) {
            if (LOG_401.getAndSet(Boolean.FALSE)) {
                this.logger().error("[ Auth ] 401 provider created handler! type = {0}", wall);
            }
        }
        return provider;
    }

    private LogOf logger() {
        return LogOf.get(this.getClass());
    }
}
