package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

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
    private YmBoot boot = new YmBoot();
    private YmServer server = new YmServer();
    private YmVertx vertx = new YmVertx();
    private YmDubbo dubbo = new YmDubbo();
    private YmApp app = new YmApp();
    private YmStorage storage = new YmStorage();
    private YmExcel excel = new YmExcel();
    private YmLogging logging = new YmLogging();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject plugins = new JsonObject();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject flyway = new JsonObject();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject sms = new JsonObject();


    public static YmConfiguration createDefault() {
        return new YmConfiguration();
    }

    public String id() {
        if (Objects.isNull(this.vertx)) {
            return null;
        }
        if (Objects.isNull(this.vertx.getApplication())) {
            return null;
        }
        return this.vertx.getApplication().getName();
    }
}
