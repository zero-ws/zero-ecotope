package io.zerows.extension.mbse.ui.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.mbse.ui.agent.service.column.UiValve;
import io.zerows.extension.mbse.ui.eon.UiMsg;
import io.zerows.extension.skeleton.spi.UiAnchoret;
import io.zerows.extension.skeleton.spi.UiApeak;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

/*
 * Bridge design for call internal actual column service
 * 1. Dynamic Apeak
 * 2. Static Apeak
 */
public class ExColumnApeak extends UiAnchoret<UiApeak> implements UiApeak {

    @Override
    public Future<JsonArray> fetchFull(final JsonObject params) {
        LOG.Ui.info(this.getLogger(), UiMsg.COLUMN_FULL, params.encodePrettily());
        final Boolean dynamic = params.getBoolean(UiApeak.ARG0);
        /* Ui valve initialization */
        final UiValve valve;
        if (dynamic) {
            valve = UiValve.dynamic();
        } else {
            valve = UiValve.fixed();
        }
        /* Whether this module used dynamic column here */
        final String identifier = params.getString(UiApeak.ARG1);
        final String sigma = params.getString(UiApeak.ARG2);
        final KView view = KView.smart(params.getValue(UiApeak.ARG3));
        return valve.fetchColumn(view, identifier, sigma);
    }
}
