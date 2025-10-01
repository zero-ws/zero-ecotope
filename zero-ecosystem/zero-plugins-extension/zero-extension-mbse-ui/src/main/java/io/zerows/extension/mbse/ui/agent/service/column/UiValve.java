package io.zerows.extension.mbse.ui.agent.service.column;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.corpus.domain.atom.commune.Vis;

/*
 * Apeak choice for different usage,
 * valve means small door to do selection on mode
 */
public interface UiValve {

    Cc<String, UiValve> CC_VALVE = Cc.open();

    static UiValve dynamic() {
        return CC_VALVE.pick(StoreValve::new, StoreValve.class.getName());
        // return FnZero.po?l(Pool.VALVE_MAP, StoreValve.class.getName(), StoreValve::new);
    }

    static UiValve fixed() {
        return CC_VALVE.pick(FileValve::new, FileValve.class.getName());
        // return FnZero.po?l(Pool.VALVE_MAP, FileValve.class.getName(), FileValve::new);
    }

    Future<JsonArray> fetchColumn(Vis view, String identifier, String sigma);
}
