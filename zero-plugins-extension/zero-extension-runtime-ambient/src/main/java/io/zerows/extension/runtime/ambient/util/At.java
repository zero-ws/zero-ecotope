package io.zerows.extension.runtime.ambient.util;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.uca.log.Log;
import io.zerows.core.uca.log.LogModule;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivity;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityChange;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XNumber;
import io.zerows.extension.runtime.ambient.osgi.spi.extension.AppInit;
import io.zerows.extension.runtime.ambient.osgi.spi.extension.DatabaseInit;
import io.zerows.extension.runtime.ambient.osgi.spi.extension.DatumInit;
import io.zerows.extension.runtime.ambient.osgi.spi.extension.SourceInit;
import io.zerows.extension.runtime.skeleton.osgi.spi.extension.Init;
import io.zerows.specification.modeling.HAtom;
import io.zerows.unity.Ux;

import java.util.List;

/*
 * Tool class available in current service only
 */
public class At {

    private static final Cc<String, Init> CC_INIT = Cc.open();

    public static Init initApp() {
        return CC_INIT.pick(AppInit::new, AppInit.class.getName());
        // return Fx.po?l(Pool.INIT_POOL, AppInit.class.getName(), AppInit::new);
    }

    public static Init initSource() {
        return CC_INIT.pick(SourceInit::new, SourceInit.class.getName());
        // return Fx.po?l(Pool.INIT_POOL, SourceInit.class.getName(), SourceInit::new);
    }

    public static Init initDatabase() {
        return CC_INIT.pick(DatabaseInit::new, DatabaseInit.class.getName());
        // return Fx.po?l(Pool.INIT_POOL, DatabaseInit.class.getName(), DatabaseInit::new);
    }

    public static Init initData() {
        return CC_INIT.pick(DatumInit::new, DatumInit.class.getName());
        // return Fx.po?l(Pool.INIT_POOL, DatumInit.class.getName(), DatumInit::new);
    }

    public static List<String> generate(final XNumber number, final Integer count) {
        return AtSerial.generate(number, count);
    }

    public static Future<List<String>> generateAsync(final XNumber number, final Integer count) {
        return Ux.future(AtSerial.generate(number, count));
    }

    public static XNumber serialAdjust(final XNumber number, final Integer count) {
        return AtSerial.adjust(number, count);
    }

    public static Future<Buffer> fileDownload(final JsonArray attachment) {
        return AtFs.fileDownload(attachment);
    }

    public static Future<Buffer> fileDownload(final JsonObject attachment) {
        return AtFs.fileDownload(attachment);
    }

    public static Future<JsonArray> fileUpload(final JsonArray attachment) {
        return AtFs.fileUpload(attachment);
    }

    public static Future<JsonArray> fileRemove(final JsonArray attachment) {
        return AtFs.fileRemove(attachment);
    }

    public static Future<JsonArray> fileDir(final JsonArray attachment, final JsonObject params) {
        return AtFsDir.fileDir(attachment, params);
    }

    public static Future<JsonObject> fileMeta(final JsonObject appJ) {
        return AtFs.fileMeta(appJ);
    }

    public static List<XActivityChange> diffChange(final JsonObject recordO, final JsonObject recordN, final HAtom atom) {
        return AtDiffer.diff(recordO, recordN, atom);
    }

    public static List<XActivityChange> diffChange(final List<XActivityChange> changes, final XActivity activity) {
        return AtDiffer.diff(changes, activity);
    }

    public interface LOG {
        String MODULE = "περιβάλλων";

        LogModule App = Log.modulat(MODULE).extension("App");
        LogModule File = Log.modulat(MODULE).extension("File");
        LogModule Flow = Log.modulat(MODULE).extension("Flow");
        LogModule HES = Log.modulat(MODULE).extension("HES");
        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Tabb = Log.modulat(MODULE).extension("Tabb");
    }
}
