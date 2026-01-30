package io.zerows.epoch.spec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private YmLogging logging = new YmLogging();

    @JsonIgnore
    @Accessors(fluent = true, chain = true)
    private ConcurrentMap<String, JsonObject> extension = new ConcurrentHashMap<>();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject plugins = new JsonObject();

    @JsonIgnore
    private String id;

    public static YmConfiguration createDefault() {
        return new YmConfiguration();
    }

    public YmConfiguration put(final String configKey, final JsonObject config) {
        this.extension.put(configKey, config);
        return this;
    }

    public String id() {
        if (Objects.nonNull(this.id)) {
            return this.id;
        }
        if (Objects.nonNull(this.vertx) && Objects.nonNull(this.vertx.getApplication())) {
            this.id = this.vertx.getApplication().getName();
        }
        if (Objects.isNull(this.id)) {
            this.id = Ut.randomString(24);
        }
        return this.id;
    }

    public InPreVertx.Config config() {
        return Objects.requireNonNull(this.vertx).getConfig();
    }

    public YmApplication application() {
        return Objects.requireNonNull(this.vertx).getApplication();
    }
}
