package io.zerows.extension.module.ui.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.ui.boot.MDUIManager;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UiValveFile implements UiValve {
    private static final MDUIManager MANAGER = MDUIManager.of();

    @Override
    public Future<JsonArray> fetchColumn(final KView view, final String identifier, final String sigma) {
        /* Default column JsonArray */
        final JsonArray columns = MANAGER.getColumn(identifier);
        log.info("{} 视图列 / sigma = {}, size = {}, columns = {}",
            UiConstant.K_PREFIX_UI, sigma, columns.size(), columns.encode());
        return Ux.future(columns);
    }
}
