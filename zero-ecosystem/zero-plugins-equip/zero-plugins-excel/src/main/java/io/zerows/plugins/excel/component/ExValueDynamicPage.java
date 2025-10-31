package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-06-26
 */
public class ExValueDynamicPage implements ExValue {

    /*
     * 格式如下
     * {
     *     "__type__": "PAGE",
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
                valueJ.put(ExcelConstant.K_TYPE, ExcelConstant.CELL.P_PAGE);

                final JsonObject content = new JsonObject();
                /*
                 * 解决系统中无法读取配置的问题，针对现有系统进行格式化处理
                 */
                content.put(KName.PATH, path);
                valueJ.put(ExcelConstant.K_CONTENT, content);
                literal = valueJ.encodePrettily();
                this.logger().info("[ Έξοδος ] （ExJson）Page = {0}, Page Value built `{1}`", path, literal);
            }
        }
        return literal;
    }
}
