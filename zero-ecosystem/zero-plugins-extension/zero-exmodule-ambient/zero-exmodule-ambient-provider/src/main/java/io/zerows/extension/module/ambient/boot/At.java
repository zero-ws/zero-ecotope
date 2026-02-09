package io.zerows.extension.module.ambient.boot;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivity;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivityChange;
import io.zerows.extension.module.ambient.domain.tables.pojos.XNumber;
import io.zerows.extension.module.ambient.spi.ExInitApp;
import io.zerows.extension.module.ambient.spi.ExInitDatabase;
import io.zerows.extension.module.ambient.spi.ExInitDatum;
import io.zerows.extension.module.ambient.spi.ExInitSource;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.HAtom;

import java.util.List;

/*
 * Tool class available in current service only
 */
public class At {

    private static final Cc<String, ExInit> CC_INIT = Cc.open();

    public static ExInit initApp() {
        return CC_INIT.pick(ExInitApp::new, ExInitApp.class.getName());
        // return FnZero.po?l(Pool.INIT_POOL, AppInit.class.getName(), AppInit::new);
    }

    public static ExInit initSource() {
        return CC_INIT.pick(ExInitSource::new, ExInitSource.class.getName());
        // return FnZero.po?l(Pool.INIT_POOL, SourceInit.class.getName(), SourceInit::new);
    }

    public static ExInit initDatabase() {
        return CC_INIT.pick(ExInitDatabase::new, ExInitDatabase.class.getName());
        // return FnZero.po?l(Pool.INIT_POOL, DatabaseInit.class.getName(), DatabaseInit::new);
    }

    public static ExInit initData() {
        return CC_INIT.pick(ExInitDatum::new, ExInitDatum.class.getName());
        // return FnZero.po?l(Pool.INIT_POOL, DatumInit.class.getName(), DatumInit::new);
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
        return AtFs.fileDir(attachment, params);
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
