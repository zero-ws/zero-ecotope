package io.zerows.metadata.datamation;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.enums.EmAop;
import io.zerows.support.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「字段映射配置」
 * 针对当前所需信息的完整字段映射定义，此定义包含了当前模型的完整定义，主要映射包括
 * <pre><code>
 *     1. 从 from -> to 的字段映射
 *     2. 从 to -> from 的字段映射
 * </code></pre>
 * 数据结构中会将映射方向分成两部分
 * <pre><code>
 *     {
 *         "field1": "to2",
 *         "field2": "to2",
 *         "identifier": {
 *             "field3": "to3",
 *             "field4": "to4"
 *         }
 *     }
 *     上述抽象格式还可以写成
 *     {
 *         {@link KMapping},                // 根模型
 *         "identifier": {@link KMapping},  // 子模型
 *     }
 * </code></pre>
 * 职中的映射部分会根据上述数据结构不同分成两大块
 * <pre><code>
 *     1. 如果是 String = String 的部分，则表示它属于根节点
 *     2. 如果是 String = {@link JsonObject} 的部分，则它属于子节点
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KMap implements Serializable {
    /** 根节点 {@link KMapping} */
    private final KMapping root = new KMapping();

    /** 子模型 identifier = {@link KMapping} */
    private final ConcurrentMap<String, KMapping> mapping =
        new ConcurrentHashMap<>();

    /**
     * 作用效果，作用效果主要用于 zero-jet 中的通道定义部分
     * <pre><code>
     *     - NONE：不产生任何影响
     *     - BEFORE：前置影响，在写数据库之前的映射转换
     *     - AFTER：后置影响，在写数据库之后的映射转换
     *     - AROUND：环绕影响，在写入数据库前后都会存在的映射转换
     * </code></pre>
     */
    private EmAop.Effect mode = EmAop.Effect.NONE;

    /**
     * 当前配置对应的映射组件，最终对应底层的 MAPPING_COMPONENT 中的配置
     */
    private Class<?> component;

    public KMap() {
    }

    public KMap(final JsonObject input) {
        this.init(input);
    }

    public KMap init(final JsonObject input) {
        if (UtBase.isNotNil(input)) {
            /*
             * Mix data structure for
             * {
             *     "String": {},
             *     "String": "String",
             *     "String": {}
             * }
             */
            this.root.init(input);
            /*
             * Content mapping `Map` here
             */
            input.fieldNames().stream()
                /* Only stored JsonObject value here */
                .filter(field -> input.getValue(field) instanceof JsonObject)
                .forEach(field -> {
                    final JsonObject fieldValue = input.getJsonObject(field);
                    /* Init here */
                    if (UtBase.isNotNil(fieldValue)) {
                        /* InJson mapping here */
                        final KMapping item = new KMapping(fieldValue);
                        this.mapping.put(field, item);
                    }
                });
        }
        return this;
    }

    /*
     * 1) MappingMode
     * 2) Class<?>
     * 3) DualMapping ( Bind Life Cycle )
     * 4) valid() -> boolean Check whether the mapping is enabled.
     */
    public EmAop.Effect getMode() {
        return this.mode;
    }

    public Class<?> getComponent() {
        return this.component;
    }

    public KMap bind(final EmAop.Effect mode) {
        this.mode = mode;
        return this;
    }

    public KMap bind(final Class<?> component) {
        this.component = component;
        return this;
    }

    public boolean valid() {
        return EmAop.Effect.NONE != this.mode;
    }

    // -------------  Get by identifier ----------------------------
    /*
     * Child get here
     */
    public KMapping child(final String key) {
        final KMapping selected = this.mapping.get(key);
        if (Objects.isNull(selected) || selected.isEmpty()) {
            return this.root;
        } else {
            return selected;
        }
    }

    public KMapping child() {
        return this.root;
    }

    // -------------  Root Method here for split instead -----------
    /*
     * from -> to, to values to String[]
     */
    public String[] to() {
        return this.root.to();
    }

    public String[] from() {
        return this.root.from();
    }

    public Set<String> to(final JsonArray keys) {
        return this.root.to(keys);
    }

    public Set<String> from(final JsonArray keys) {
        return this.root.from(keys);
    }

    /*
     * Get value by from key, get to value
     */
    public String to(final String from) {
        return this.root.to(from);
    }

    public Class<?> toType(final String from) {
        return this.root.toType(from);
    }

    public boolean fromKey(final String key) {
        return this.root.fromKey(key);
    }

    public String from(final String to) {
        return this.root.from(to);
    }

    public Class<?> fromType(final String to) {
        return this.root.fromType(to);
    }

    public boolean toKey(final String key) {
        return this.root.toKey(key);
    }

    /*
     * from -> to, from keys
     */
    public Set<String> keys() {
        return this.root.keys();
    }

    public Set<String> values() {
        return this.root.values();
    }

    @Override
    public String toString() {
        return "KMap{" +
            "root=" + this.root +
            ", mapping=" + this.mapping +
            ", mode=" + this.mode +
            ", component=" + this.component +
            '}';
    }
}
