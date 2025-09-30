package io.zerows.core.uca.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VName;
import io.zerows.ams.util.HUt;

/**
 * @author lang : 2024-06-26
 */
class MetaAtFile implements MetaAt {
    MetaAtFile() {
    }

    @Override
    public JsonObject loadContent(final JsonObject metadataJ) {
        final String path = HUt.valueString(metadataJ, VName.PATH);
        if (HUt.isNil(path)) {
            return new JsonObject();
        }
        try {
            return HUt.ioJObject(path);
        } catch (final Throwable ex) {
            return new JsonObject();
        }
    }
}
