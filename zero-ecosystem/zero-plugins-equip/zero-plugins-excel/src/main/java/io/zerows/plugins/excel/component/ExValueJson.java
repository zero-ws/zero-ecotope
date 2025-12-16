package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;

/*
 * Fix issue of excel length: 32767 characters
 */
@SuppressWarnings("all")
@Slf4j
public class ExValueJson implements ExValue {

    @Override
    public String to(final Object value, final ConcurrentMap<String, String> paramMap) {
        final String[] pathArr = value.toString().split(VString.COLON);
        String literal = value.toString();
        if (2 == pathArr.length) {
            final String path = pathArr[1];
            if (Ut.isNotNil(path)) {
                final String content = Ut.ioString(path.trim());
                if (Ut.isNotNil(content)) {
                    // 日志级别调整
                    log.debug("{} (ExJson) 文件：{}, 捕捉的值：{}", ExcelConstant.K_PREFIX, path, content);
                    if (Ut.isJArray(content)) {
                        final JsonArray normalized = Ut.toJArray(content);
                        literal = normalized.encodePrettily();
                    } else if (Ut.isJObject(content)) {
                        final JsonObject normalized = Ut.toJObject(content);
                        literal = normalized.encodePrettily();
                    }
                } else {
                    log.warn("{}（ExJson) File = {} 遇到未可知的异常!!", ExcelConstant.K_PREFIX, path);
                }
            }
        }
        return literal;
    }
}
