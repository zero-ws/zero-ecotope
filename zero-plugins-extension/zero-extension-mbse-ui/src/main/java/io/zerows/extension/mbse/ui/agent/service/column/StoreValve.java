package io.zerows.extension.mbse.ui.agent.service.column;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.Fx;
import io.zerows.unity.Ux;
import io.zerows.ams.constant.VString;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.ui.domain.tables.daos.UiColumnDao;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiColumn;
import io.zerows.module.domain.atom.commune.Vis;

import java.util.Comparator;
import java.util.stream.Collectors;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

class StoreValve implements UiValve {

    @Override
    public Future<JsonArray> fetchColumn(final Vis vis, final String identifier, final String sigma) {
        /*
         * Default global controlId is
         * 1) The format is VIEW-identifier
         * 2) sigma could distinguish multi applications
         */
        final String controlId = vis.view() + "-" + identifier;
        final JsonObject filters = new JsonObject();
        filters.put(VString.EMPTY, Boolean.TRUE);
        filters.put("controlId", controlId);
        filters.put(KName.SIGMA, sigma);
        LOG.Ui.info(this.getClass(), "The condition for column fetching: {0}", filters.encode());
        return Ux.Jooq.on(UiColumnDao.class)
            .<UiColumn>fetchAsync(filters)
            .compose(list -> Ux.future(list.stream()
                /*
                 * Position Sorting
                 */
                .sorted(Comparator.comparing(UiColumn::getPosition))
                .collect(Collectors.toList())))
            .compose(list -> Ux.future(list.stream().map(this::procColumn).collect(Collectors.toList())))
            .compose(jsonList -> Ux.future(new JsonArray(jsonList)));
    }

    private JsonObject procColumn(final UiColumn column) {
        final JsonObject columnJson = new JsonObject();
        columnJson.put("title", column.getTitle());
        columnJson.put("dataIndex", column.getDataIndex());
        /*
         * sorter
         * className
         * fixed
         * width
         */
        Fx.monad(column::getSorter, (sorter) -> columnJson.put("sorter", sorter));
        Fx.monad(column::getFixed, (fixed) -> {
            if (fixed) {
                columnJson.put("fixed", "left");
            } else {
                columnJson.put("fixed", "right");
            }
        });
        Fx.monad(column::getClassName, (className) -> columnJson.put("className", className));
        Fx.monad(column::getWidth, (width) -> columnJson.put("width", width));
        /*
         * If render
         */
        Fx.monad(column::getRender, (render) -> {
            columnJson.put("$render", render);
            if ("DATE".equals(render)) {
                assert null != column.getFormat() : " $format should not be null when DATE";
                columnJson.put("$format", column.getFormat());
            } else if ("DATUM".equals(render)) {
                // columnJson.put("$datum")
                assert null != column.getDatum() : " $datum should not be null when DATUM";
                columnJson.put("$datum", column.getDatum());
            }
        });
        Fx.monad(column::getFilterType, (filterType) -> {
            columnJson.put("$filter.type", filterType);
            columnJson.put("$filter.config", column.getFilterConfig());
            Ut.valueToJObject(columnJson, "$filter.config");
        });
        /*
         * Zero Config
         */
        Fx.monad(column::getEmpty, (empty) -> columnJson.put("$empty", empty));
        Fx.monad(column::getMapping, (mapping) -> {
            columnJson.put("$mapping", mapping);
            Ut.valueToJObject(columnJson, "$mapping");
        });
        Fx.monad(column::getConfig, (config) -> {
            columnJson.put("$config", config);
            Ut.valueToJObject(columnJson, "$config");
        });
        return columnJson;
    }
}
