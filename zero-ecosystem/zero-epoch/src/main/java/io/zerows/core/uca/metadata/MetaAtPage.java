package io.zerows.core.uca.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VName;
import io.zerows.ams.util.HUt;

/**
 * @author lang : 2024-06-26
 */
class MetaAtPage implements MetaAt {
    MetaAtPage() {
    }

    @Override
    public JsonObject loadContent(final JsonObject metadataJ) {
        final String path = HUt.valueString(metadataJ, VName.PATH);
        if (HUt.isNil(path)) {
            return new JsonObject();
        }

        final MetaCachePage page = MetaCachePage.singleton();
        return page.get(path);
    }
}
