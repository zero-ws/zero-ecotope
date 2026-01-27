package io.zerows.epoch.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * Cloud 配置要根据 vertx-boot.yml 来定义，所以此处配置不可以直接处理，只能等待 vertx-boot.yml 解析后再进行注入
 * 此处是切换 单机环境 和 云环境的关键点，内部构造当前配置对象来选择使用哪个组件进行配置加载，加载之后是原始字符串，直
 * 接根据原始字符串进行基础配置解析
 *
 * @author lang : 2025-10-06
 */
@Data
public class InPreVertx implements Serializable {
    private YmCloud cloud;
    private Config config;
    private YmApplication application = new YmApplication();

    @Data
    public static class Config implements Serializable {
        private static final String KEY_IMPORT = "import";

        @JsonProperty(KEY_IMPORT)
        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray imports = new JsonArray();
    }
}
