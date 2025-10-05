package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 片段配置，对应 component / config 两个节点部分，可提供核心配置信息
 * <pre>
 *     component
 *     config
 * </pre>
 * 由于此配置可通用，所以在此处以 Unit 做类前缀，主要是辅助序列化处理，保证可以被扫描到
 *
 * @author lang : 2025-10-05
 */
@Data
public class UnitComponent implements Serializable {

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> component;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config;
}
