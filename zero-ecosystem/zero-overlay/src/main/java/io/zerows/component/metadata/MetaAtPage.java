package io.zerows.component.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.support.UtBase;

/**
 * @author lang : 2024-06-26
 */
class MetaAtPage implements MetaAt {
    MetaAtPage() {
    }

    @Override
    public JsonObject loadContent(final JsonObject metadataJ) {
        final String path = UtBase.valueString(metadataJ, VName.PATH);
        if (UtBase.isNil(path)) {
            return new JsonObject();
        }

        final MetaCachePage page = MetaCachePage.singleton();
        return page.get(path);
    }
}
