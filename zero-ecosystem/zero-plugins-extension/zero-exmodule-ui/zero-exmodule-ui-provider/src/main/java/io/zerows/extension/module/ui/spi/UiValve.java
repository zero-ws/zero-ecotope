package io.zerows.extension.module.ui.spi;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.metadata.KView;

/*
 * Apeak choice for different usage,
 * valve means small door to do selection join mode
 */
public interface UiValve {

    Cc<String, UiValve> CC_VALVE = Cc.open();

    static UiValve dynamic() {
        return CC_VALVE.pick(UiValveStore::new, UiValveStore.class.getName());
        // return FnZero.po?l(Pool.VALVE_MAP, StoreValve.class.getName(), StoreValve::new);
    }

    static UiValve fixed() {
        return CC_VALVE.pick(UiValveFile::new, UiValveFile.class.getName());
        // return FnZero.po?l(Pool.VALVE_MAP, FileValve.class.getName(), FileValve::new);
    }

    Future<JsonArray> fetchColumn(KView view, String identifier, String sigma);
}
