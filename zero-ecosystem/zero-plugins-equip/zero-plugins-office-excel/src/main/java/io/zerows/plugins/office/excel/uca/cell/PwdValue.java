package io.zerows.plugins.office.excel.uca.cell;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;

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
