package io.zerows.plugins.office.excel.uca.cell;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.plugins.office.excel.eon.ExConstant;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class DynamicFileValue implements ExValue {
    /*
     * 格式如下
     * {
     *     "__type__": "FILE",
     *     "__content__": {
     *         "path": "<page-uri>"
     *     }
     * }
     */
    @Override
    public Object to(final Object value, final ConcurrentMap<String, String> paramMap) {
        final String[] pathArr = value.toString().split(VString.COLON);
        String literal = value.toString();
        if (2 == pathArr.length) {
            final String path = pathArr[1];
            if (Ut.isNotNil(path)) {
                final JsonObject valueJ = new JsonObject();
                valueJ.put(ExConstant.K_TYPE, ExConstant.CELL.P_FILE);

                final JsonObject content = new JsonObject();
                content.put(KName.PATH, path);
                valueJ.put(ExConstant.K_CONTENT, content);
                literal = valueJ.encodePrettily();
                this.logger().info("[ Έξοδος ] （ExJson）File = {0}, File Value built `{1}`", path, literal);
            }
        }
        return literal;
    }
}
