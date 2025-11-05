package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.Future;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.plugins.JtConsumer;
import io.zerows.mbse.sdk.Commercial;

/**
 * 「Consumer」
 * Default consumer to consume request, complex code logical
 */
public class JtAiakos implements JtConsumer {

    private transient final JtMonitor monitor = JtMonitor.create(this.getClass());

    /*
     * Data example
     */
    @Override
    public Future<Envelop> async(final Envelop envelop, final Commercial commercial) {
        return JtPandora.async(envelop, commercial, this.monitor);
    }
}
