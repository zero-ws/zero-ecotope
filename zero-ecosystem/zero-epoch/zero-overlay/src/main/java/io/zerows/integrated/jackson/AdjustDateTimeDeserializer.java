package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.zerows.support.base.UtBase;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 「Tp」Jackson 反序列化器
 * <p>
 * 该反序列化器继承自 `LocalDateTimeDeserializer`，用于处理带时区的日期时间计算。
 * 修复了标准 ISO 格式中由于空格或 T 分隔符导致的解析异常。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AdjustDateTimeDeserializer extends LocalDateTimeDeserializer {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public AdjustDateTimeDeserializer() {
        super(DEFAULT_FORMATTER);
    }

    @Override
    public LocalDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        if (parser.hasTokenId(6)) {
            final String string = parser.getText().trim();
            if (string.isEmpty()) {
                return null;
            } else {
                try {
                    /*
                     * 核心修复逻辑：
                     * 1. 如果使用的是默认格式器 (ISO_LOCAL_DATE_TIME)，则启用“旁路智能解析”模式。
                     * 2. UtBase.parse 会自动识别以下格式：
                     * - 2025-12-26 18:37:14 (带空格，业务常用)
                     * - 2025-12-26T18:37:14 (标准 ISO)
                     * - 2025-12-26T18:37:14Z (带 UTC 标识)
                     */
                    if (this._formatter == DEFAULT_FORMATTER) {
                        // 利用 UtBase 的智能识别能力，兼容 Z 结尾和空格分隔符
                        return UtBase.toDateTime(UtBase.parse(string));
                    } else {
                        // 如果显式指定了自定义格式器，则走严格解析匹配
                        return LocalDateTime.parse(string, this._formatter);
                    }
                } catch (final Exception var12) {
                    /*
                     * 修复编译错误：
                     * 如果捕获的不是 DateTimeException，则将其包装成 DateTimeException
                     * 这样才能匹配基类方法 _handleDateTimeException 的签名
                     */
                    final DateTimeException dte = (var12 instanceof DateTimeException) ?
                        (DateTimeException) var12 : new DateTimeException(var12.getMessage(), var12);

                    return this._handleDateTimeException(context, dte, string);
                }
            }
        } else {
            // 处理数组格式 [2025, 12, 26, 18, 37, 14]
            if (parser.isExpectedStartArrayToken()) {
                return this.deserializeFromArray(parser, context);
            }

            if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                return (LocalDateTime) parser.getEmbeddedObject();
            } else {
                if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    this._throwNoNumericTimestampNeedTimeZone(parser, context);
                }
                return (LocalDateTime) this._handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
            }
        }
    }

    /**
     * 内部抽取：处理从数组反序列化 LocalDateTime 的逻辑
     */
    private LocalDateTime deserializeFromArray(final JsonParser parser, final DeserializationContext context) throws IOException {
        JsonToken t = parser.nextToken();
        if (t == JsonToken.END_ARRAY) {
            return null;
        }

        if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) &&
            context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            final LocalDateTime result = this.deserialize(parser, context);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                this.handleMissingEndArrayForSingle(parser, context);
            }
            return result;
        }

        if (t == JsonToken.VALUE_NUMBER_INT) {
            final int year = parser.getIntValue();
            final int month = parser.nextIntValue(-1);
            final int day = parser.nextIntValue(-1);
            final int hour = parser.nextIntValue(-1);
            final int minute = parser.nextIntValue(-1);
            t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return LocalDateTime.of(year, month, day, hour, minute);
            } else {
                final int second = parser.getIntValue();
                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return LocalDateTime.of(year, month, day, hour, minute, second);
                } else {
                    int partialSecond = parser.getIntValue();
                    if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                        partialSecond *= 1000000;
                    }
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                    }
                    return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                }
            }
        }
        return context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
    }
}