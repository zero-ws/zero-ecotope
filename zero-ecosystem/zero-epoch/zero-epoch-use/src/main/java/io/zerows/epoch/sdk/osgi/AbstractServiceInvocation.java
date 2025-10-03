package io.zerows.epoch.sdk.osgi;

import io.vertx.core.Future;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-07-01
 */
public abstract class AbstractServiceInvocation implements ServiceInvocation {
    private final Bundle provider;

    protected AbstractServiceInvocation(final Bundle provider) {
        this.provider = provider;
    }

    @Override
    public Bundle provider() {
        return this.provider;
    }

    @Override
    public Future<Boolean> stop(final ServiceContext context) {
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected <T> T service(final Class<T> classT) {
        return Ut.Bnd.service(classT, this.provider);
    }
}
