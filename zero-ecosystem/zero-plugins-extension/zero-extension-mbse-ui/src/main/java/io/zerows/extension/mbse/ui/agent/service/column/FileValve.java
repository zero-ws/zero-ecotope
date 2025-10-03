package io.zerows.extension.mbse.ui.agent.service.column;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.program.Ux;
import io.zerows.epoch.metadata.commune.Vis;
import io.zerows.extension.mbse.ui.bootstrap.UiPin;
import io.zerows.extension.mbse.ui.eon.UiMsg;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

class FileValve implements UiValve {

    @Override
    public Future<JsonArray> fetchColumn(final Vis view, final String identifier, final String sigma) {
        /* Default column JsonArray */
        final JsonArray columns = UiPin.getColumn(identifier);
        LOG.Ui.info(this.getClass(), UiMsg.COLUMN_STATIC, sigma, columns.size(), columns.encode());
        return Ux.future(columns);
    }
}
