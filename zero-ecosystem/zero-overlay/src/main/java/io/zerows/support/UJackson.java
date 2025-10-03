package io.zerows.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.integrated.jackson.ModuleHorizon;
import io.zerows.integrated.jackson.OriginalNamingStrategy;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2023-05-09
 */
@SuppressWarnings("all")
class UJackson {
    // 使用单例的 ObjectMapper 进行序列化/反序列化操作
    private static final JsonMapper MAPPER = JsonMapper.builder()
        /*
         * Previous code
         * JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
         * JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
         * MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
         * MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
         *
         * Case Sensitive
         * Below new code logical
         */
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
        .build();

    static {
        // 配置项：忽略空值
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Non-standard JSON but we allow C style comments in our JSON
        // 配置项：允许注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // 配置项：关闭日期作为时间戳
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 配置项：忽略未知的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Big Decimal
        // 配置项：使用 BigDecimal 替代 float/double
        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        // 配置自定义命名策略
        MAPPER.setPropertyNamingStrategy(OriginalNamingStrategy.JOOQ_NAME);

        // 注册自定义模块
        final ModuleHorizon module = new ModuleHorizon();
        MAPPER.registerModule(module);
    }

    static JsonMapper mapper() {
        return MAPPER.copy();
    }

    static <T> String serialize(final T t) {
        return Fn.jvmOr(() -> MAPPER.writeValueAsString(t));
    }

    static <T, R extends Iterable> R serializeJson(final T t) {
        final String content = serialize(t);
        if (content.trim().startsWith(VString.LEFT_BRACE)) {
            return (R) new JsonObject(content);
        } else {
            return (R) new JsonArray(content);
        }
    }

    static <T> T deserialize(final JsonObject value, final Class<T> type) {
        return deserialize(value.encode(), type);
    }

    static <T> T deserialize(final JsonArray value, final Class<T> type) {
        return deserialize(value.encode(), type);
    }

    static <T> List<T> deserialize(final JsonArray value, final TypeReference<List<T>> type) {
        return deserialize(value.encode(), type);
    }

    static <T> T deserialize(final String value, final Class<T> type) {
        if (Objects.isNull(value)) {
            return null;
        }
        return Fn.jvmOr(() -> MAPPER.readValue(value, type));
    }

    static <T> T deserialize(final String value, final TypeReference<T> type) {
        if (Objects.isNull(value)) {
            return null;
        }
        return Fn.jvmOr(() -> MAPPER.readValue(value, type));
    }

}
