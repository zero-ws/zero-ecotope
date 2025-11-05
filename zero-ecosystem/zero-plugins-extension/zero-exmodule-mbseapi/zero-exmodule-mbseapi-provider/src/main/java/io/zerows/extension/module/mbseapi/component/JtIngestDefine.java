package io.zerows.extension.module.mbseapi.component;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.exception._80401Exception501IngestMissing;
import io.zerows.extension.module.mbseapi.exception._80402Exception501IngestSpec;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.management.OZeroStore;
import io.zerows.support.Ut;

import java.util.Objects;

class JtIngestDefine implements JtIngest {
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
