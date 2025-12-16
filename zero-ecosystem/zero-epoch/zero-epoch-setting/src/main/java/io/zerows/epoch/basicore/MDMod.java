package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.SourceReflect;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.boot.ConfigMod;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * <pre>
 *     模块的基础配置位置
 *     plugins/{mid}.yml
 *     格式：
 *     shape:
 *       id:                    对应 {@link MDId}
 *       name:
 *     supplier:
 *       comment:
 *       component:
 *       config:
 * </pre>
 *
 * @author lang : 2025-12-15
 */
@Data
@Slf4j
public class MDMod implements Serializable {

    public String name() {
        return Objects.requireNonNull(this.shape).name;
    }

    private Shape shape;
    private Supplier supplier;

    @Data
    public static class Shape implements Serializable {
        private String id;
        private String name;
    }

    @Data
    public static class Supplier implements Serializable {
        private String comment;
        @JsonSerialize(using = ClassSerializer.class)
        @JsonDeserialize(using = ClassDeserializer.class)
        private Class<?> component;
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject config;
    }

    public ConfigMod configurer() {
        if (Objects.isNull(this.supplier) || Objects.isNull(this.supplier.getComponent())) {
            return ConfigMod.of();
        }
        final Class<?> classImpl = this.supplier.getComponent();
        if (!SourceReflect.isImplement(classImpl, ConfigMod.class)) {
            log.warn("[ ZERO ] 模块配置类未实现 ConfigMod 接口，使用默认配置处理器: {}", classImpl.getName());
            return ConfigMod.of();
        }
        return ConfigMod.of(classImpl);
    }
}
