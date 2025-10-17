package io.zerows.epoch.metadata;

import io.r2mo.typed.cc.Cc;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.support.Ut;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/*
 * [Data Structure]
 * Define mapping for custom serialization/deserialization
 * This data structure is bind to `pojo/xxx.yml` file for model mapping
 * field -> column here.
 * It must be used with Mojo.
 */
@Deprecated
public class MMPojoMapping {

    private static final String POJO = "pojo/{0}.yml";
    private static final Cc<String, MMPojo> CC_MOJO = Cc.open();
    private final LogO logger;
    private final JsonObject converted = new JsonObject();
    private MMPojo mojo;
    private JsonObject data = new JsonObject();

    private MMPojoMapping(final Class<?> clazz) {
        this.logger = Ut.Log.metadata(clazz);
    }

    public static MMPojoMapping create(final Class<?> clazz) {
        return new MMPojoMapping(clazz);
    }

    public MMPojoMapping mount(final String filename) {
        // Build metadata, pick(supplier, filename)
        this.mojo = CC_MOJO.pick(() -> {
            this.logger.info("Mount pojo configuration file {0}", filename);
            final JsonObject data = Ut.ioYaml(MessageFormat.format(POJO, filename));

            /* Only one point to refer `pojoFile` */
            return Ut.deserialize(data, MMPojo.class).on(filename);
        }, filename);
        return this;
    }

    public MMPojoMapping type(final Class<?> entityCls) {
        this.mojo.setType(entityCls);
        return this;
    }

    public MMPojoMapping connect(final JsonObject data) {
        // Copy new data
        this.data = data.copy();
        return this;
    }

    public MMPojoMapping to() {
        this.convert(this.mojo.getOut());
        return this;
    }

    public MMPojo mojo() {
        return this.mojo;
    }

    private void convert(final ConcurrentMap<String, String> mapper) {
        Observable.fromIterable(this.data.fieldNames())
            .groupBy(mapper::containsKey)
            .map(contain -> Boolean.TRUE.equals(contain.getKey()) ?
                contain.subscribe(from -> {
                    // Existing in mapper
                    final String to = mapper.get(from);
                    this.converted.put(to, this.data.getValue(from));
                }) :
                contain.subscribe(item ->
                    // Not found in mapper
                    this.converted.put(item, this.data.getValue(item)))
            ).subscribe().dispose();
    }

    public MMPojoMapping from() {
        this.convert(this.mojo.getIn());
        return this;
    }

    public MMPojoMapping apply(final Function<String, String> function) {
        final JsonObject result = this.data.copy();
        result.forEach((entry) ->
            this.converted.put(function.apply(entry.getKey()),
                entry.getValue()));
        return this;
    }

    public JsonObject json(final Object entity, final boolean overwrite) {
        final JsonObject data = Ut.serializeJson(entity);
        final JsonObject merged = this.converted.copy();
        for (final String field : data.fieldNames()) {
            if (overwrite) {
                // If overwrite
                merged.put(field, data.getValue(field));
            } else {
                if (!merged.containsKey(field)) {
                    merged.put(field, data.getValue(field));
                }
            }
        }
        return merged;
    }


    @SuppressWarnings("unchecked")
    public <T> T get() {
        final Object reference = Ut.deserialize(this.converted, this.mojo.getType(), true);
        return (T) reference;
    }

    public JsonObject result() {
        return this.converted;
    }
}
