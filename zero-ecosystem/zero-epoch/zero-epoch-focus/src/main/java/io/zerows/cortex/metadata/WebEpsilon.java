package io.zerows.cortex.metadata;

import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.enums.EmWeb;
import lombok.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * get container to getNull parameters
 */
@SuppressWarnings("unchecked")
@Data
public class WebEpsilon<T> implements Serializable {

    private Object defaultValue;
    private String name;
    private EmWeb.MimeParser mime;
    private Class<?> argType;
    private Annotation annotation;

    private T value;

    public void setName(final String name) {
        if (KWeb.ARGS.MIME_DIRECT.equals(name)) {
            this.mime = EmWeb.MimeParser.RESOLVER;
        } else if (KWeb.ARGS.MIME_IGNORE.equals(name)) {
            this.mime = EmWeb.MimeParser.TYPED;
        } else {
            this.mime = EmWeb.MimeParser.STANDARD;
        }
        this.name = name;
    }

    public T getValue() {
        if (null == this.value) {
            if (null == this.defaultValue) {
                return null;
            } else {
                return (T) this.defaultValue;
            }
        } else {
            return this.value;
        }
    }

    public WebEpsilon<T> setValue(final T value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "Epsilon{" +
            "name='" + this.name + '\'' +
            ", mime=" + this.mime +
            ", argType=" + this.argType +
            ", defaultValue=" + this.defaultValue +
            ", annotation=" + this.annotation +
            ", get=" + this.value +
            '}';
    }
}
