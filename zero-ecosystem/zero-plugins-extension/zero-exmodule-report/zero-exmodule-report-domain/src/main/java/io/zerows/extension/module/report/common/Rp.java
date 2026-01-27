package io.zerows.extension.module.report.common;

import io.vertx.core.json.JsonObject;


/**
 * @author lang : 2024-11-14
 */
public final class Rp {

    private Rp() {
    }

    /**
     * <pre><code>
     *     mode 取值
     *     TODAY                - 当前日期 {@link java.time.LocalDate}
     *     TOMORROW             - 明天日期 {@link java.time.LocalDate}
     *     NOW                  - 当前时间 {@link java.time.LocalDateTime}
     *     NOW_TOMORROW         - 明天时间 {@link java.time.LocalDateTime}
     *     TIME                 - 时间戳 {@link java.time.Instant}
     * </code></pre>
     *
     * @param mode   模式说明
     * @param params 参数
     * @return 解析之后的值
     */
    public static Object valueFormat(final String mode, final JsonObject params) {
        return RpValue.format(mode, params);
    }

    public static JsonObject valueFormat(final JsonObject paramsTpl, final JsonObject params) {
        final JsonObject output = new JsonObject();
        paramsTpl.fieldNames().forEach(field -> {
            final String mode = paramsTpl.getString(field);
            final Object value = RpValue.format(mode, params);
            output.put(field, value);
        });
        return output;
    }
}
