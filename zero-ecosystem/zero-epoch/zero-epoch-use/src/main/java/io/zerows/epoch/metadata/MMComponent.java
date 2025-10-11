package io.zerows.epoch.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
public class MMComponent implements Serializable {
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private final JsonObject config = new JsonObject();
    @JsonIgnore
    private String key;
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> component;

    public MMComponent() {
    }


    // ---------------- 静态专用方法 -------------------
    public static MMComponent create(final String key, final Class<?> component) {
        final MMComponent instance = new MMComponent();
        instance.key = key;
        instance.setComponent(component);
        return instance;
    }


    // ---------------- Fluent绑定方法 -------------------
    public MMComponent bind(final JsonObject config) {
        this.setConfig(config);
        return this;
    }

    public MMComponent bind(final String key) {
        this.key = key;
        return this;
    }

    public MMComponent bind(final Class<?> component) {
        this.setComponent(component);
        return this;
    }

    // ---------------- Java Bean方法 -------------------

    public JsonObject getConfig() {
        return this.config.copy();
    }

    public void setConfig(final JsonObject config) {
        if (Ut.isNotNil(config)) {
            this.config.mergeIn(config, true);
        }
    }

    // ---------------- 特殊API -------------------
    /* 实例化创建一个 class = component 的实例（直接实例化方便执行）*/
    public <T> T instance(final Class<?> interfaceCls, final Object... args) {
        final boolean valid = this.isImplement(interfaceCls);
        if (valid) {
            return Ut.instance(this.component, args);
        } else {
            log.warn("[ ZERO ] 组件 componentCls = {} 和接口 `{}` 类型冲突", this.component, interfaceCls);
            return null;
        }
    }

    public boolean isImplement(final Class<?> interfaceCls) {
        if (Objects.isNull(this.component)) {
            return false;
        }
        return Ut.isImplement(this.component, interfaceCls);
    }

    public String key() {
        return this.key;
    }

    /*
     * 字典管理中会使用的唯一键
     * Source Key = Component Name + Configuration
     * 1. Component Name means the same component
     * 2. Configuration means different configuration.
     * 维度：组件名 + 配置的HashCode
     */
    public String keyUnique() {
        return this.component.getName() + ":" + this.config.hashCode();
    }
}
