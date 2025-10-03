package io.zerows.component.shared.program;

import java.io.Serializable;

/**
 * 带类型的专用数据集
 * name = alias
 * name = Class
 * name = Object
 * 属性集
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KVar implements Serializable {
    private final String name;
    private String alias;
    private Class<?> type;
    private Object value;

    protected KVar(final String name) {
        this.name = name;
        this.alias = name;
        this.type = String.class;
        this.value = null;
    }

    public static KVar of(final String name) {
        return new KVar(name);
    }
    
    @SuppressWarnings("unchecked")
    public <C extends KVar> C bind(final String alias) {
        this.alias = alias;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public <C extends KVar> C bind(final Class<?> type) {
        this.type = type;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public <C extends KVar> C value(final Object value) {
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
}
