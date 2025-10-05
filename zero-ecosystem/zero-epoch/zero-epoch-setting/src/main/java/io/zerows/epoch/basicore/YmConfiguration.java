package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 针对 vertx.yml 和 vertx-boot.yml 的配置选择专用类，底层可直接获取，选取序列化模式构造基于当前节点的核心配置信息
 * <pre>
 *     boot: {@link YmBoot}
 *     server: {@link YmServer}
 * </pre>
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmConfiguration implements Serializable {
    private YmBoot boot;
    private YmServer server;
    private YmVertx vertx;
    private YmDubbo dubbo;
    private YmApp app;
    private YmStorage storage;
    private YmExcel excel;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject plugins;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject flyway;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject sms;
}
