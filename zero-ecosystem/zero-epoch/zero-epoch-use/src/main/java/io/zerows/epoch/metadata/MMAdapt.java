package io.zerows.epoch.metadata;

import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 序列化和反序列化的向量对象，用于处理映射信息：`pojo/xxx.yml` 文件
 * <pre>
 *     type:
 *     mapping:
 *       fieldT: fieldJ
 * </pre>
 *
 * @author lang : 2025-10-17
 */
@Slf4j
public class MMAdapt {
    private static final String DEFAULT_YML = "pojo/{0}.yml";
    private static final Cc<String, R2Vector> CC_VECTOR = Cc.open();
    private static final Cc<String, MMAdapt> CC_REF = Cc.openThread();
    private final R2Vector vector;

    private MMAdapt(final String filename) {
        this.vector = CC_VECTOR.pick(() -> {
            log.info("[ ZERO ] 从文件加载 MMVector: {}", filename);
            final String filepath = MessageFormat.format(DEFAULT_YML, filename);
            return R2Vector.of(filepath);
        }, filename);
    }

    public static MMAdapt of(final String filename) {
        Objects.requireNonNull(filename, "[ ZERO ] 传入文件名不可为 null.");
        return CC_REF.pick(() -> new MMAdapt(filename), filename);
    }

    public MMAdapt ofType(final Class<?> clazz) {
        this.vector.setType(clazz);
        return this;
    }

    public String mapBy(final String key) {
        return this.vector.mapBy(key);
    }

    public String mapTo(final String key) {
        return this.vector.mapTo(key);
    }

    /**
     * 比如 T -> Json
     * <pre>
     *     zSigma -> sigma
     *     zName -> name
     *     此处是反序的操作
     *     - in = sigma,        out = zSigma
     *     - in = name,         out = zName
     * </pre>
     */
    public void mapBy(final Predicate<String> keyFn, final BiConsumer<String, String> entryFn) {
        this.vector.mapBy((k, v) -> keyFn.test(k), entryFn);
    }

    public JsonObject mapBy(final JsonObject data) {
        return this.mapping(data, this.vector.mapBy());
    }

    @SuppressWarnings("all")
    public <T> T mapByT(final JsonObject data) {
        final JsonObject converted = this.mapBy(data);
        return (T) Ut.deserialize(converted, this.vector.getType(), true);
    }

    public void mapTo(final Predicate<String> keyFn, final BiConsumer<String, String> entryFn) {
        this.vector.mapTo((k, v) -> keyFn.test(k), entryFn);
    }

    public JsonObject mapTo(final JsonObject data) {
        return this.mapping(data, this.vector.mapTo());
    }

    @SuppressWarnings("all")
    public <T> T mapToT(final JsonObject data) {
        final JsonObject converted = this.mapTo(data);
        return (T) Ut.deserialize(converted, this.vector.getType(), true);
    }

    private JsonObject mapping(final JsonObject data, final ConcurrentMap<String, String> mapping) {
        final JsonObject converted = new JsonObject();
        Observable.fromIterable(data.fieldNames()).groupBy(mapping::containsKey)
            .map(item -> Boolean.TRUE.equals(item.getKey()) ?
                item.subscribe(from -> converted.put(mapping.get(from) /* from -> to */, data.getValue(from))) :
                item.subscribe(from -> converted.put(from, data.getValue(from)))
            ).subscribe();
        return converted;
    }
}
