package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.vertx.core.json.JsonArray;

import java.io.IOException;
import java.util.List;

/**
 * # 「Tp」Jackson Deserializer (Optimized for Array)
 * <p>
 * Optimized deserializer that handles both Array and String inputs for JsonArray,
 * and avoids double-parsing overhead.
 * <pre>
 * 解决类似 JsonObject 的反序列化问题，支持：
 * 1. 标准 JSON 数组: [1, 2, "a"]
 * 2. 字符串化数组: "[1, 2, \"a\"]"
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

    @Override
    @SuppressWarnings("unchecked")
    public JsonArray deserialize(final JsonParser parser,
                                 final DeserializationContext context)
        throws IOException {

        // 1. 获取当前 Token
        final JsonToken currentToken = parser.currentToken();

        // -------------------------------------------------------
        // 情况 A: 标准 JSON 数组 (最常见，性能最优路径)
        // 输入: [1, 2, "val"]
        // -------------------------------------------------------
        if (currentToken == JsonToken.START_ARRAY) {
            // 直接读取为 List，避免 readTree -> toString -> parse 的三次倒手
            // Vert.x JsonArray 本质就是 List<Object> 的封装
            final List<Object> list = (List<Object>) parser.readValueAs(List.class);
            return new JsonArray(list);
        }

        // -------------------------------------------------------
        // 情况 B: JSON 字符串 (解决 Stringified JSON 问题)
        // 输入: "[1, 2, \"val\"]"
        // -------------------------------------------------------
        if (currentToken == JsonToken.VALUE_STRING) {
            // getText() 获取的是去掉了引号和转义符的“干净”字符串
            final String jsonContent = parser.getText();

            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                return new JsonArray();
            }

            try {
                // 此时 jsonContent 是 [1,2...]，可以被安全解析
                return new JsonArray(jsonContent);
            } catch (final Exception e) {
                // 如果字符串内容不是合法的 JSON Array，返回空数组
                // 实际业务中可视情况决定是否抛出异常
                return new JsonArray();
            }
        }

        // -------------------------------------------------------
        // 情况 C: 其他情况 (Null 等)
        // -------------------------------------------------------
        return null;
    }
}