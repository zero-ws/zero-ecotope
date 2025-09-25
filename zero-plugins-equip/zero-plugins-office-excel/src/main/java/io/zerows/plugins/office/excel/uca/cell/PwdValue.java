package io.zerows.plugins.office.excel.uca.cell;

import io.zerows.ams.constant.VPath;
import io.zerows.ams.constant.VString;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;

import java.util.concurrent.ConcurrentMap;

/**
 * PWD Processing
 */
public class PwdValue implements ExValue {

    @Override
    @SuppressWarnings("all")
    public Object to(final Object value, final ConcurrentMap<String, String> paramMap) {
        final String pathRoot = paramMap.get(KName.DIRECTORY);
        final String field = paramMap.get(KName.FIELD);
        final String filepath = Ut.ioPath(pathRoot, field) + VString.DOT + VPath.SUFFIX.JSON;
        final JsonObject content = Ut.ioJObject(filepath);
        return content.encode();
    }
}
