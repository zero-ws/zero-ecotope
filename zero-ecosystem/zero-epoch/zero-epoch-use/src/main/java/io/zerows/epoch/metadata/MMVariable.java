package io.zerows.epoch.metadata;

import io.zerows.support.base.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 带类型的专用数据集
 * name = alias
 * name = Class
 * name = Object
 * 属性集
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MMVariable implements Serializable {
    private final String name;
    private String alias;
    private Class<?> type;
    private Object value;

    protected MMVariable(final String name) {
        this.name = name;
        this.alias = name;
        this.type = String.class;
        this.value = null;
    }

    public static MMVariable of(final String name) {
        return new MMVariable(name);
    }

    @SuppressWarnings("unchecked")
    public <C extends MMVariable> C bind(final String alias) {
        this.alias = alias;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public <C extends MMVariable> C bind(final Class<?> type) {
        this.type = type;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public <C extends MMVariable> C value(final Object value) {
        this.value = value;
        return (C) this;
    }

    public String name() {
        return this.name;
    }

    public Class<?> type() {
        return this.type;
    }

    public String alias() {
        return this.alias;
    }

    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) this.value;
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public static class Set implements Serializable {

        private final ConcurrentMap<String, MMVariable> attrMap = new ConcurrentHashMap<>();

        private Set() {
        }

        public static Set of(final ConcurrentMap<String, MMVariable> data) {
            final Set set = new Set();
            set.bind(data);
            return set;
        }

        public static Set of() {
            return of(new ConcurrentHashMap<>());
        }

        public Set bind(final ConcurrentMap<String, MMVariable> data) {
            if (Objects.nonNull(data)) {
                this.attrMap.putAll(data);
            }
            return this;
        }

        public Set save(final String name, final String alias) {
            return this.saveWith(name, alias, String.class, null);
        }

        public Set save(final String name, final String alias, final Class<?> type) {
            return this.saveWith(name, alias, type, null);
        }

        public Set saveWith(final String name, final String alias,
                            final Object value) {
            return this.saveWith(name, alias, String.class, value);
        }

        public Set saveWith(final String name, final String alias,
                            final Class<?> type, final Object value) {
            final MMVariable attr;
            if (this.attrMap.containsKey(name)) {
                attr = this.attrMap.get(name);
            } else {
                attr = MMVariable.of(name);
            }
            attr.bind(Objects.isNull(type) ? String.class : type);
            if (UtBase.isNotNil(alias)) {
                attr.bind(alias);
            }
            attr.value(value);
            this.attrMap.put(name, attr);
            return this;
        }

        public Set remove(final String name) {
            this.attrMap.remove(name);
            return this;
        }

        public MMVariable attribute(final String name) {
            return this.attrMap.getOrDefault(name, null);
        }

        public java.util.Set<String> names() {
            return this.attrMap.keySet();
        }
    }
}
