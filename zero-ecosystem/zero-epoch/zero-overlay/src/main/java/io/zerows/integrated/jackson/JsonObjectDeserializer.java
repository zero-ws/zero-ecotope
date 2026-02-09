package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.Map;

/**
 * # 「Tp」Jackson Deserializer (Optimized)
 * <p>
 * Optimized deserializer that handles both Object and String inputs,
 * and avoids double-parsing overhead.
 * <pre>
 *     强化解决
 *  at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 1]
 * 	at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:76)
 * 	at com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1801)
 * 	at com.fasterxml.jackson.databind.DeserializationContext.handleMissingInstantiator(DeserializationContext.java:1426)
 * 	at com.fasterxml.jackson.databind.deser.std.StdDeserializer._deserializeFromString(StdDeserializer.java:310)
 * 	at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:455)
 * 	at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserialize(MapDeserializer.java:31)
 * 	at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:342)
 * 	at com.fasterxml.jackson.databind.ObjectMapper._readValue(ObjectMapper.java:5023)
 * 	at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3104)
 * 	at io.vertx.core.json.jackson.DatabindCodec.fromParser(DatabindCodec.java:112)
 * 	... 62 common frames omitted
 * </pre>
 */
public class JsonObjectDeserializer extends JsonDeserializer<JsonObject> {

    @Override
    @SuppressWarnings("unchecked")
    public JsonObject deserialize(final JsonParser parser,
                                  final DeserializationContext context)
        throws IOException {

        // 1. 获取当前 Token
        final JsonToken currentToken = parser.currentToken();

        // -------------------------------------------------------
        // 情况 A: 标准 JSON 对象 (最常见，性能最优路径)
        // 输入: {"id": 1, "name": "X"}
        // -------------------------------------------------------
        if (currentToken == JsonToken.START_OBJECT) {
            // 直接读取为 Map，避免 readTree -> toString -> parse 的三次倒手
            // Vert.x JsonObject 本质就是 Map<String, Object> 的封装
            final Map<String, Object> map = (Map<String, Object>) parser.readValueAs(Map.class);
            return new JsonObject(map);
        }

        // -------------------------------------------------------
        // 情况 B: JSON 字符串 (导致你报错的场景)
        // 输入: "{\"id\": 1, \"name\": \"X\"}"
        // -------------------------------------------------------
        if (currentToken == JsonToken.VALUE_STRING) {
            // getText() 获取的是去掉了引号和转义符的“干净”字符串
            final String jsonContent = parser.getText();

            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                return new JsonObject();
            }

            try {
                // 此时 jsonContent 是 {"id":1...}，可以被安全解析
                return new JsonObject(jsonContent);
            } catch (final Exception e) {
                // 如果字符串内容不是合法的 JSON，返回空对象或抛出更清晰的异常
                // log.warn("String value is not a valid JSON Object: " + jsonContent);
                return new JsonObject();
            }
        }

        // -------------------------------------------------------
        // 情况 C: 其他情况 (Null, Array 等)
        // -------------------------------------------------------
        return null;
    }
}