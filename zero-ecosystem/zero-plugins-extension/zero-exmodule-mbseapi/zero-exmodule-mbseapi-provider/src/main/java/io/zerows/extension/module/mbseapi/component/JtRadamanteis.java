package io.zerows.extension.module.mbseapi.component;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

/**
 * 「Ingest」
 * findRunning extraction when paramMode = DEFINE, it's valid
 */
public class JtRadamanteis implements JtIngest {
    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        // TODO: JtIngest code logical
        return null;
    }
}
