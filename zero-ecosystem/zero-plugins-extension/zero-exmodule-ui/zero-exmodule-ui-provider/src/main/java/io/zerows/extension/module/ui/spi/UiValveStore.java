package io.zerows.extension.module.ui.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ui.domain.tables.daos.UiColumnDao;
import io.zerows.extension.module.ui.domain.tables.pojos.UiColumn;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.zerows.extension.module.ui.boot.Ui.LOG;

class UiValveStore implements UiValve {

    @Override
    public Future<JsonArray> fetchColumn(final KView vis, final String identifier, final String sigma) {
        /*
         * Default global controlId is
         * 1) The formatFail is VIEW-identifier
         * 2) sigma could distinguish multi applications
         */
        final String controlId = vis.view() + "-" + identifier;
        final JsonObject filters = new JsonObject();
        filters.put(VString.EMPTY, Boolean.TRUE);
        filters.put("controlId", controlId);
        filters.put(KName.SIGMA, sigma);
        LOG.Ui.info(this.getClass(), "The condition for column fetching: {0}", filters.encode());
        return DB.on(UiColumnDao.class)
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
        if (Objects.nonNull(column.getSorter())) {
            columnJson.put("sorter", column.getSorter());
        }
        if (Objects.nonNull(column.getFixed())) {
            if (column.getFixed()) {
                columnJson.put("fixed", "left");
            } else {
                columnJson.put("fixed", "right");
            }
        }
        if (Objects.nonNull(column.getClassName())) {
            columnJson.put("className", column.getClassName());
        }
        if (Objects.nonNull(column.getWidth())) {
            columnJson.put("width", column.getWidth());
        }
        /*
         * If render
         */
        if (Objects.nonNull(column.getRender())) {
            final String render = column.getRender();
            columnJson.put("$render", render);
            if ("DATE".equals(render)) {
                assert null != column.getFormat() : " $formatFail should not be null when DATE";
                columnJson.put("$formatFail", column.getFormat());
            } else if ("DATUM".equals(render)) {
                // columnJson.put("$datum")
                assert null != column.getDatum() : " $datum should not be null when DATUM";
                columnJson.put("$datum", column.getDatum());
            }
        }
        if (Objects.nonNull(column.getFilterType())) {
            columnJson.put("$filter.type", column.getFilterType());
            columnJson.put("$filter.config", column.getFilterConfig());
            Ut.valueToJObject(columnJson, "$filter.config");
        }
        /*
         * Zero Config
         */
        if (Objects.nonNull(column.getEmpty())) {
            columnJson.put("$empty", column.getEmpty());
        }
        if (Objects.nonNull(column.getMapping())) {
            columnJson.put("$mapping", column.getMapping());
            Ut.valueToJObject(columnJson, "$mapping");
        }
        if (Objects.nonNull(column.getConfig())) {
            columnJson.put("$config", column.getConfig());
            Ut.valueToJObject(columnJson, "$config");
        }
        return columnJson;
    }
}
