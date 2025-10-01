package io.zerows.extension.mbse.action.uca.param;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.eon.JtConstant;
import io.zerows.extension.mbse.action.exception._80401Exception501IngestMissing;
import io.zerows.extension.mbse.action.exception._80402Exception501IngestSpec;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;
import io.zerows.epoch.corpus.metadata.store.OZeroStore;

import java.util.Objects;

class DefineIngest implements JtIngest {
    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        final Class<?> clazz = OZeroStore.classInject(JtConstant.COMPONENT_INGEST_KEY);
        if (Objects.isNull(clazz)) {
            return Envelop.failure(new _80401Exception501IngestMissing());
        } else if (!Ut.isImplement(clazz, JtIngest.class)) {
            return Envelop.failure(new _80402Exception501IngestSpec(clazz.getName()));
        } else {
            final JtIngest ingest = Ut.instance(clazz);
            return ingest.in(context, uri);
        }
    }
}
