package io.zerows.extension.module.ui.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.extension.module.ui.domain.tables.daos.UiLayoutDao;
import io.zerows.extension.module.ui.domain.tables.daos.UiPageDao;
import io.zerows.extension.module.ui.domain.tables.pojos.UiPage;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import jakarta.inject.Inject;

import java.util.Objects;
import java.util.function.Function;

public class PageService implements PageStub {
    @Inject
    private transient ControlStub controlStub;

    @Override
    public Future<JsonObject> fetchLayout(final String layoutId) {
        /*
         * Enable Cache for Layout
         */
        final Function<String, Future<JsonObject>> executor = (layout) ->
            DB.on(UiLayoutDao.class)
                .fetchByIdAsync(layout)
                .compose(Ux::futureJ)
                /*
                 * Configuration converted to InJson
                 */
                .compose(Fx.ofJObject(KName.Ui.CONFIG));
        // Ui Cache Enabled
        return Rapid.<String, JsonObject>object(UiConstant.POOL_LAYOUT)
            .cached(layoutId, () -> executor.apply(layoutId));
    }

    @Override
    public Future<JsonObject> fetchAmp(final String sigma,
                                       final JsonObject params) {
        final JsonObject filters = params.copy();
        filters.put(KName.SIGMA, sigma);
        filters.put("", Boolean.TRUE);
        return DB.on(UiPageDao.class)
            .<UiPage>fetchOneAsync(filters)
            .compose(page -> {
                if (Objects.nonNull(page)) {
                    /*
                     * Page Existing in current system
                     */
                    if (Ut.isNotNil(page.getLayoutId())) {
                        /*
                         * Continue to extract layout Data here
                         */
                        return this.fetchLayout(page);
                    } else {
                        return Ux.futureJ(page);
                    }
                } else {
                    /*
                     * No configuration related to current page
                     */
                    return Ux.future(new JsonObject());
                }
            })
            .compose(pageJson -> {
                /*
                 * Extract pageId
                 */
                final String pageId = pageJson.getString(KName.KEY);
                return this.controlStub.fetchControls(pageId)
                    /*
                     * Fetch Controls of current page
                     * This will be filled into $control variable
                     */
                    .compose(controls -> {
                        /*
                         * Grouped by key, this could be used in front tier directly
                         */
                        final JsonObject converted = new JsonObject();
                        controls.stream()
                            .filter(Objects::nonNull)
                            .map(item -> (JsonObject) item)
                            .filter(item -> Objects.nonNull(item.getString(KName.KEY)))
                            .forEach(item -> converted.put(item.getString(KName.KEY), item.copy()));
                        pageJson.put(KName.Ui.CONTROLS, converted);
                        return Ux.future(pageJson);
                    });
            });
    }

    /*
     * Fetch layout by page.
     */
    private Future<JsonObject> fetchLayout(final UiPage page) {
        return this.fetchLayout(page.getLayoutId())
            .compose(layout -> {
                final JsonObject pageJson = Ux.toJson(page);
                pageJson.put("layout", layout);
                return Fx.ofJObject(
                    KName.Ui.CONTAINER_CONFIG,
                    KName.Ui.ASSIST,
                    KName.Ui.GRID
                ).apply(pageJson);
                /*
                 * Configuration converted to InJson
                 */
                //.compose(Ke.mount(KName.Ui.CONTAINER_CONFIG))
                //.compose(Ke.mount(KName.Ui.ASSIST))
                /*
                 * Another method to convert JsonArray
                 */
                //.compose(Ke.mountArray(KName.Ui.GRID));
            });
    }
}
