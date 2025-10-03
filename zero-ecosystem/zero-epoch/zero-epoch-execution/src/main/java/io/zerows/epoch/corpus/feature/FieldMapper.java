package io.zerows.epoch.corpus.feature;

import io.vertx.core.json.JsonObject;
import io.zerows.metadata.datamation.KMapping;
import io.zerows.epoch.program.Ut;

public class FieldMapper implements Mapper {

    private final transient boolean keepNil;

    public FieldMapper() {
        this(true);
    }

    public FieldMapper(final boolean keepNil) {
        this.keepNil = keepNil;
    }

    @Override
    public JsonObject in(final JsonObject in, final KMapping mapping) {
        return Ut.aiIn(in, mapping, this.keepNil);
    }

    @Override
    public JsonObject out(final JsonObject out, final KMapping mapping) {
        return Ut.aiOut(out, mapping, this.keepNil);
    }
}
