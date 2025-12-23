package io.zerows.extension.module.ui.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.extension.module.ui.spi.UiValve;
import io.zerows.extension.skeleton.spi.UiAnchoret;
import io.zerows.extension.skeleton.spi.UiApeak;
import lombok.extern.slf4j.Slf4j;

/*
 * Bridge design for call internal actual column service
 * 1. Dynamic Apeak
 * 2. Static Apeak
 */
@Slf4j
public class UiApeakColumn extends UiAnchoret<UiApeak> implements UiApeak {

    @Override
    public Future<JsonArray> fetchFull(final JsonObject params) {
        log.info("{} 全列 / params = {}", UiConstant.K_PREFIX_UI, params.encode());
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
