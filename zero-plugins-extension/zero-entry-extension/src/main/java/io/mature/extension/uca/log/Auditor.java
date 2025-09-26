package io.mature.extension.uca.log;

import io.zerows.core.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;

import java.util.Queue;
import java.util.Set;

/**
 * 记录专用
 *
 * 1. AuditorTodo（待确认单构造）
 * 2. AuditorTrue（变更历史 active = true）
 * 3. AuditorTrace（变更历史 active = false）
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Auditor {

    static Auditor history(final DataAtom atom, final JsonObject options) {
        final String catchKey = Ut.keyAtom(atom, options);
        return AuditorHistory.CC_AUDITOR.pick(() -> new AuditorHistory(options).bind(atom), catchKey);
        // RFn.po?l(AuditorHistory.POOL_HISTORY, argument.atomKey(options), () -> new AuditorHistory(options).bind(argument));
    }

    Auditor bind(DataAtom atom);

    Future<JsonObject> trackAsync(JsonObject recordN, JsonObject recordO,
                                  String serial, Set<String> ignoreSet);

    default Future<JsonObject> trackAsync(final JsonObject twins,
                                          final String serial, final Set<String> ignoreSet) {
        return this.trackAsync(
            twins.getJsonObject(KName.__.NEW), twins.getJsonObject(KName.__.OLD),
            serial, ignoreSet
        );
    }

    /*
     * 默认不开放批量行为
     */
    default Future<JsonArray> trackAsync(final JsonArray recordN, final JsonArray recordO,
                                         final Queue<String> serial, final Set<String> ignoreSet) {
        return Ut.Bnd.failOut(_501NotSupportException.class, this.getClass());
    }
}
