package io.zerows.extension.runtime.crud.uca.input.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.mbse.metadata.KColumn;
import io.zerows.mbse.metadata.KModule;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.program.Ux;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ApeakPre implements Pre {
    /*
     * {
     *      "identifier": "column identifier bind",
     *      "dynamic": "Whether use dynamic mode to get column",
     *      "view": "DEFAULT, the view name"
     * }
     */
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final KModule module = in.module();
        /* Column definition */
        final KColumn column = module.getColumn();
        assert null != column : "The column definition should not be null";
        /*
         * In static mode, identifier could found ui file
         * In dynamic mode, identifier & sigma could let system fetch columns
         * from database directly.
         * Here add new parameter `view` for future usage to support multi views
         */
        data.put(KName.IDENTIFIER, column.getIdentifier());
        data.put(KName.DYNAMIC, column.getDynamic());
        this.viewProc(data, column);
        return Ux.future(data);
    }

    protected void viewProc(final JsonObject data, final KColumn column) {
        if (Objects.isNull(data.getValue(KName.VIEW))) {
            // Vis: Fix bug of default view
            data.put(KName.VIEW, KView.smart(column.getView()));
        }
    }
}
