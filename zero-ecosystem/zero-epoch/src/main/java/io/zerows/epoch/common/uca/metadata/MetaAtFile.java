package io.zerows.epoch.common.uca.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VName;
import io.zerows.ams.util.UtBase;

/**
 * @author lang : 2024-06-26
 */
class MetaAtFile implements MetaAt {
    MetaAtFile() {
    }

    @Override
    public JsonObject loadContent(final JsonObject metadataJ) {
        final String path = UtBase.valueString(metadataJ, VName.PATH);
        if (UtBase.isNil(path)) {
            return new JsonObject();
        }
        try {
            return UtBase.ioJObject(path);
        } catch (final Throwable ex) {
            return new JsonObject();
        }
    }
}
