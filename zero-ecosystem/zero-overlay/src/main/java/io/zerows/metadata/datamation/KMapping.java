package io.zerows.metadata.datamation;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.UtBase;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 「字段双向映射」
 * 此类用于双向字段映射，可在两个字典中进行相互转换
 * <pre><code>
 *     {
 *         "from field": "to field"
 *     }
 *     最终构造结构如
 *     vector:      from        =       to
 *     revert:      to          =       from
 *               vectorType             toType
 * </code></pre>
 * 由此，字典本身可在不同的属性上不断转换
 * <pre><code>
 *     1. vectorType为主，从 from -> to    ( 存储在 vector )
 *     2. revertType为主，从 to -> from    ( 存储在 revert )
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KMapping implements Serializable {
    private final ConcurrentMap<String, String> vector =
        new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> revert =
        new ConcurrentHashMap<>();
    /*
     * Involve expression for type parsing here
     * It means that we need type attribute to do conversation
     */
    private final ConcurrentMap<String, Class<?>> vectorType =
        new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Class<?>> revertType =
        new ConcurrentHashMap<>();

    KMapping() {
    }

    public KMapping(final JsonObject input) {
        this.init(input);
    }

    public boolean isEmpty() {
        return this.vector.isEmpty();
    }

    void init(final JsonObject input) {
        if (UtBase.isNotNil(input)) {
            input.fieldNames().stream()
                /* Only stored string value here */
                .filter(field -> input.getValue(field) instanceof String)
                .forEach(field -> {
                    final String to = input.getString(field);
                    if (0 < to.indexOf(',')) {
                        /* To expression */
                        final String[] toArray = to.split(",");
                        final String toField = Objects.isNull(toArray[0]) ? null : toArray[0].trim();
                        final String typeFlag = Objects.isNull(toArray[1]) ? "" : toArray[1].trim();
                        if (Objects.nonNull(toField)) {
                            /*
                             * Type here
                             */
                            final Class<?> type = KMapType.type(typeFlag);
                            /* mapping type */
                            this.vectorType.put(field, type);
                            this.revertType.put(toField, type);
                            /* mapping */
                            this.vector.put(field, toField);
                            /* revert */
                            this.revert.put(toField, field);
                        }
                    } else {
                        /* mapping */
                        this.vector.put(field, to);
                        /* revert */
                        this.revert.put(to, field);
                    }
                });
        }
    }

    public KMapping bind(final ConcurrentMap<String, Class<?>> typeMap) {
        this.vector.keySet().forEach((field) -> {
            if (typeMap.containsKey(field)) {
                this.vectorType.put(field, typeMap.get(field));
                final String revertField = this.vector.get(field);
                this.revertType.put(revertField, typeMap.get(field));
            }
        });
        return this;
    }

    /*
     * from -> to, to values to String[]
     */
    public String[] to() {
        return this.vector.values().toArray(new String[]{});
    }

    public String[] from() {
        return this.revert.values().toArray(new String[]{});
    }

    public Set<String> to(final JsonArray keys) {
        return keys.stream().filter(item -> item instanceof String)
            .map(item -> (String) item)
            .map(this.vector::get)
            .collect(Collectors.toSet());
    }

    public Set<String> from(final JsonArray keys) {
        return keys.stream().filter(item -> item instanceof String)
            .map(item -> (String) item)
            .map(this.revert::get)
            .collect(Collectors.toSet());
    }

    /*
     * Get value by from key, get to value
     */
    public String to(final String from) {
        return this.vector.get(from);
    }

    public Class<?> toType(final String from) {
        return this.vectorType.get(from);
    }

    public boolean fromKey(final String key) {
        return this.vector.containsKey(key);
    }

    public String from(final String to) {
        return this.revert.get(to);
    }

    public Class<?> fromType(final String to) {
        return this.revertType.get(to);
    }

    public boolean toKey(final String key) {
        return this.revert.containsKey(key);
    }

    /*
     * from -> to, from keys
     */
    public Set<String> keys() {
        return this.vector.keySet();
    }

    public Set<String> values() {
        return new HashSet<>(this.vector.values());
    }

    @Override
    public String toString() {
        return "KMapping{" +
            "vector=" + this.vector +
            ", revert=" + this.revert +
            '}';
    }
}
