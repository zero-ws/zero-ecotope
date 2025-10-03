package io.zerows.plugins.office.excel.uca.cell;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.component.environment.DevEnv;
import io.zerows.constant.VString;
import io.zerows.epoch.program.Ut;

import java.util.concurrent.ConcurrentMap;

/*
 * Fix issue of excel length: 32767 characters
 */
@SuppressWarnings("all")
public class PrefixJsonValue implements ExValue {

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
                    if (DevEnv.devExcelRange()) {
                        this.logger().info("[ Έξοδος ] （ExJson）File = {0}, InJson Value captured `{1}`",
                            path, content);
                    }
                    if (Ut.isJArray(content)) {
                        final JsonArray normalized = Ut.toJArray(content);
                        literal = normalized.encodePrettily();
                    } else if (Ut.isJObject(content)) {
                        final JsonObject normalized = Ut.toJObject(content);
                        literal = normalized.encodePrettily();
                    }
                } else {
                    this.logger().warn("[ Έξοδος ] （ExJson) File = {0} met error!!", path);
                }
            }
        }
        return literal;
    }
}
