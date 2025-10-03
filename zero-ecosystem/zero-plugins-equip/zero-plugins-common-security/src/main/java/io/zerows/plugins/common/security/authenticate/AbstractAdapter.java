package io.zerows.plugins.common.security.authenticate;

import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.component.log.Annal;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.epoch.corpus.security.AegisItem;
import io.zerows.enums.EmSecure;
import io.zerows.epoch.program.Ut;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractAdapter implements AdapterProvider {
    private static final AtomicBoolean LOG_401 = new AtomicBoolean(Boolean.TRUE);

    protected AuthenticationProvider provider401Internal(final Aegis aegis) {
        final AegisItem item = aegis.item();
        final Class<?> providerCls = item.getProviderAuthenticate();
        if (Objects.isNull(providerCls)) {
            return null;
        }
        final EmSecure.AuthWall wall = aegis.getType();
        final AuthenticationProvider provider = Ut.invokeStatic(providerCls, YmlCore.secure.PROVIDER, aegis);
        if (Objects.isNull(provider)) {
            if (LOG_401.getAndSet(Boolean.FALSE)) {
                this.logger().error("[ Auth ] 401 provider created handler! type = {0}", wall);
            }
        }
        return provider;
    }

    private Annal logger() {
        return Annal.get(this.getClass());
    }
}
