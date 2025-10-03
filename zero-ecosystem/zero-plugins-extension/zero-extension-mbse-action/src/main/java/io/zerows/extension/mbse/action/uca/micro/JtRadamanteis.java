package io.zerows.extension.mbse.action.uca.micro;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

/**
 * 「Ingest」
 * get extraction when paramMode = DEFINE, it's valid
 */
public class JtRadamanteis implements JtIngest {
    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        // TODO: JtIngest code logical
        return null;
    }
}
