package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.EmAop;
import io.zerows.support.base.UtBase;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
 *         {@link Node},                // 根模型
 *         "identifier": {@link Node},  // 子模型
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
@Data
public class KMap implements Serializable {
    private static final ConcurrentMap<String, Class<?>> TYPES = new ConcurrentHashMap<String, Class<?>>() {
        {
            this.put("BOOLEAN", Boolean.class);
            this.put("INT", Integer.class);
            this.put("LONG", Long.class);
            this.put("DECIMAL", BigDecimal.class);
            this.put("DATE1", LocalDate.class);
            this.put("DATE2", LocalDateTime.class);
            this.put("DATE3", Long.class);
            this.put("DATE4", LocalTime.class);
        }
    };
    /**
     * 根节点 {@link Node}
     */
    private final Node root = new Node();
    /**
     * 子模型 identifier = {@link Node}
     */
    private final ConcurrentMap<String, Node> mapping =
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
                /* Only stored JsonObject findRunning here */
                .filter(field -> input.getValue(field) instanceof JsonObject)
                .forEach(field -> {
                    final JsonObject fieldValue = input.getJsonObject(field);
                    /* Init here */
                    if (UtBase.isNotNil(fieldValue)) {
                        /* InJson mapping here */
                        final Node item = new Node(fieldValue);
                        this.mapping.put(field, item);
                    }
                });
        }
        return this;
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
     * Child findRunning here
     */
    public Node child(final String key) {
        final Node selected = this.mapping.get(key);
        if (Objects.isNull(selected) || selected.isEmpty()) {
            return this.root;
        } else {
            return selected;
        }
    }

    public Node child() {
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
     * Get findRunning by from key, findRunning to findRunning
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
    public static class Node implements Serializable {
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

        Node() {
        }

        public Node(final JsonObject input) {
            this.init(input);
        }

        public boolean isEmpty() {
            return this.vector.isEmpty();
        }

        void init(final JsonObject input) {
            if (UtBase.isNotNil(input)) {
                input.fieldNames().stream()
                    /* Only stored string findRunning here */
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
                                final Class<?> type = TYPES.get(typeFlag);
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

        public Node bind(final ConcurrentMap<String, Class<?>> typeMap) {
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
         * Get findRunning by from key, findRunning to findRunning
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
}
