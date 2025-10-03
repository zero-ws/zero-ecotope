package io.zerows.metadata.program;

import io.vertx.core.json.JsonObject;
import io.zerows.component.metadata.MetaAt;
import io.zerows.enums.EmMeta;
import io.zerows.support.UtBase;

import java.io.Serializable;

/*
 * Normalized for field `metadata`
 * 1) __type__
 * 2) __content__
 */
public class KMetadata implements Serializable {

    private static final String KEY_TYPE = "__type__";
    private static final String KEY_CONTENT = "__content__";

    private final JsonObject content = new JsonObject();

    public KMetadata(final JsonObject input) {
        /*
         * Whether input contains `__type__`
         */
        if (input.containsKey(KEY_TYPE)) {
            /*
             * Source parsed here.
             */
            final EmMeta.Source source =
                UtBase.toEnum(input.getString(KEY_TYPE), EmMeta.Source.class);
            final JsonObject content = input.getJsonObject(KEY_CONTENT);
            final MetaAt metaAt = MetaAt.of(source);
            this.content.mergeIn(metaAt.loadContent(content));
        } else {
            /*
             * The pure metadata parsing, stored input to content
             * directly here.
             */
            this.content.mergeIn(input.copy(), true);
        }
    }

    public JsonObject toJson() {
        return this.content;
    }
}
