package io.zerows.integrated.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.r2mo.typed.json.jackson.JacksonRegistry;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JacksonRegistryZero implements JacksonRegistry {

    @Override
    public void afterBootstrap(final SimpleModule module) {
        // JsonArray 和 JsonObject 的序列化和反序列化
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addDeserializer(JsonObject.class, new JsonObjectDeserializer());

        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        module.addDeserializer(JsonArray.class, new JsonArrayDeserializer());
    }
}
