package io.zerows.core.uca.metadata;

import io.zerows.ams.constant.em.EmMeta;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 特殊的元数据加载模块，主要针对不同格式执行加载
 * <pre><code>
 *     metadata -> JsonObject
 * </code></pre>
 * 此处的 JsonObject 是标准化的特殊格式，可根据不同的数据来源执行外联数据加载
 *
 * @author lang : 2024-06-26
 */
public interface MetaAt {

    ConcurrentMap<EmMeta.Source, Supplier<MetaAt>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            put(EmMeta.Source.FILE, MetaAtFile::new);
            put(EmMeta.Source.PAGE, MetaAtPage::new);
        }
    };

    static MetaAt of(final EmMeta.Source source) {
        final Supplier<MetaAt> supplier = SUPPLIER.getOrDefault(source, null);
        Objects.requireNonNull(supplier);
        return supplier.get();
    }

    JsonObject loadContent(JsonObject metadataJ);
}
