package io.zerows.component.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.support.base.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 底层存储，上层 MDPage 在初始化过程中会将内容存储到这里，并且被 PAGE 类型的 metadata 解析
 *
 * @author lang : 2024-06-26
 */
public class MetaCachePage implements Serializable {
    private static MetaCachePage INSTANCE;
    private final ConcurrentMap<String, JsonObject> store = new ConcurrentHashMap<>();

    private MetaCachePage() {
    }

    public static MetaCachePage singleton() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new MetaCachePage();
        }
        return INSTANCE;
    }

    public MetaCachePage put(final String key, final JsonObject value) {
        if (UtBase.isNotNil(key) && UtBase.isNotNil(value)) {
            this.store.put(key, value);
        }
        return this;
    }

    public JsonObject get(final String key) {
        return this.store.getOrDefault(key, new JsonObject());
    }
}
