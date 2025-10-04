package io.zerows.corpus.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.corpus.model.Rule;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Rigor {

    ConcurrentMap<Class<?>, Rigor> CC_SKELETON = new ConcurrentHashMap<Class<?>, Rigor>() {
        {
            /* JsonObject & JsonArray */
            this.put(JsonObject.class, new RigorJObject());
            this.put(JsonArray.class, new RigorJArray());
            /* File & FileUpload for @Codex */
            this.put(File.class, new RigorFile());
            this.put(FileUpload.class, new RigorFile());
        }
    };

    static Rigor get(final Class<?> clazz) {
        return CC_SKELETON.get(clazz);
    }

    WebException verify(final Map<String, List<Rule>> rulers,
                        final Object value);
}
