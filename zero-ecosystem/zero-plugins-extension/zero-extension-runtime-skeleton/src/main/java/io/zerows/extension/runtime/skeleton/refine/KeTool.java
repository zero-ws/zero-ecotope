package io.zerows.extension.runtime.skeleton.refine;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.DBSActor;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

@Slf4j
class KeTool {

    private static String DATABASE;

    static String getCatalog() {
        if (Ut.isNil(DATABASE)) {
            final Database database = DBSActor.ofDatabase();
            DATABASE = database.getInstance();
        }
        return DATABASE;
    }

    static Configuration getConfiguration() {
        return DBSActor.ofDSL().configuration();
    }

    static Future<JsonObject> map(final JsonObject data, final String field,
                                  final ConcurrentMap<String, JsonObject> attachmentMap,
                                  final BiFunction<JsonObject, JsonArray, Future<JsonArray>> fileFn) {
        /*
         * Here call add only
         */
        final String key = data.getString(field);
        Objects.requireNonNull(key);
        final ConcurrentMap<String, Future<JsonArray>> futures = new ConcurrentHashMap<>();
        attachmentMap.forEach((fieldF, condition) -> {
            /*
             * Put `key` of data into `modelKey`
             */
            final JsonObject criteria = condition.copy();
            if (Ut.isNotNil(criteria)) {
                criteria.put(VString.EMPTY, Boolean.TRUE);
                criteria.put(KName.MODEL_KEY, key);
                /*
                 * JsonArray normalize
                 */
                final JsonArray dataArray = Ut.valueJArray(data, fieldF);
                Ut.itJArray(dataArray).forEach(json -> json.put(KName.MODEL_KEY, key));
                futures.put(fieldF, fileFn.apply(criteria, dataArray));
            } else {
                /*
                 * Log
                 */
                log.warn("[ ExZERO ] 查询条件不可为空！");
            }
        });
        return Fx.combineM(futures).compose(mapData -> {
            mapData.forEach(data::put);
            return Ux.future(data);
        });
    }

    static void banner(final String module) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("|                                                           |");
        System.out.println("|     Zero Extension:  " + module);
        System.out.println("|                                                           |");
        System.out.println("-------------------------------------------------------------");
    }
}
