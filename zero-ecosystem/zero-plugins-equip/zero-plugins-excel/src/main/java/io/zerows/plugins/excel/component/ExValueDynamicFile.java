package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class ExValueDynamicFile implements ExValue {
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
                valueJ.put(ExcelConstant.K_TYPE, ExcelConstant.CELL.P_FILE);

                final JsonObject content = new JsonObject();
                content.put(KName.PATH, path);
                valueJ.put(ExcelConstant.K_CONTENT, content);
                literal = valueJ.encodePrettily();
                this.logger().info("[ Έξοδος ] （ExJson）File = {0}, File Value built `{1}`", path, literal);
            }
        }
        return literal;
    }
}
