package io.zerows.extension.module.ui.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.ui.boot.UiPin;
import io.zerows.extension.module.ui.common.UiMsg;
import io.zerows.program.Ux;

import static io.zerows.extension.module.ui.boot.Ui.LOG;

class UiValveFile implements UiValve {

    @Override
    public Future<JsonArray> fetchColumn(final KView view, final String identifier, final String sigma) {
        /* Default column JsonArray */
        final JsonArray columns = UiPin.getColumn(identifier);
        LOG.Ui.info(this.getClass(), UiMsg.COLUMN_STATIC, sigma, columns.size(), columns.encode());
        return Ux.future(columns);
    }
}
