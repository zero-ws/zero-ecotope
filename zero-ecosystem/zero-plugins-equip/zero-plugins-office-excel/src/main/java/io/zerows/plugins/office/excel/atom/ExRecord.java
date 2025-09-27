package io.zerows.plugins.office.excel.atom;

import io.zerows.ams.constant.VString;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.specification.atomic.HJson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class ExRecord implements Serializable, HJson {

    private final ExTable tableRef;
    private final transient Map<String, Object> data
        = new HashMap<>();

    public ExRecord(final ExTable tableRef) {
        this.tableRef = tableRef;
    }

    public static boolean isEmpty(final JsonObject recordRef) {
        final boolean isEmpty = recordRef.isEmpty();
        if (isEmpty) {
            return true;
        } else {
            /*
             * Remove ""
             */
            final long counter = recordRef.fieldNames().stream()
                .filter(field -> Objects.nonNull(recordRef.getValue(field)))
                .filter(field -> Ut.isNotNil(recordRef.getValue(field).toString()))
                .count();
            return 0 == counter;
        }
    }

    public void put(final String field, final Object value) {
        this.data.put(field, value);
    }

    // 内部调用 put(String,Object)
    public void putOr(final JsonObject data) {
        data.fieldNames().forEach(field -> this.put(field, data.getValue(field)));
    }

    public Set<String> keySet() {
        return this.data.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String field) {
        final Object value = this.data.get(field);
        return null == value ? null : (T) value;
    }

    public boolean isEmpty() {
        return isEmpty(this.toJson());
    }

    public ExTable refTable() {
        return this.tableRef;
    }

    @Override
    public String toString() {
        final StringBuilder content = new StringBuilder();
        this.data.forEach((key, value) -> content.append(key).append(VString.EQUAL).append(value).append(VString.COMMA));
        return content.toString();
    }

    @Override
    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        this.data.forEach((key, value) -> this.consume(key, value, json::put));
        return json;
    }

    @Override
    public void fromJson(final JsonObject json) {
        if (null != json) {
            this.data.clear();
            json.stream().forEach(entry -> this.consume(entry.getKey(), entry.getValue(), this.data::put));
        }
    }

    private void consume(final String key, final Object value, final BiConsumer<String, Object> consumer) {
        if (Objects.nonNull(value)) {
            consumer.accept(key, value);
        }
    }
}
