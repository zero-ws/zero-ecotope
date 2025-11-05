package io.zerows.extension.module.ui.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ui.common.em.RowType;
import io.zerows.extension.module.ui.domain.tables.daos.UiFieldDao;
import io.zerows.extension.module.ui.domain.tables.pojos.UiField;
import io.zerows.extension.module.ui.servicespec.FieldStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static io.zerows.extension.module.ui.boot.Ui.LOG;

public class FieldService implements FieldStub {
    private static final LogOf LOGGER = LogOf.get(FieldService.class);

    @Override
    public Future<JsonArray> fetchUi(final String formId) {
        return DB.on(UiFieldDao.class)
            .<UiField>fetchAsync(KName.Ui.CONTROL_ID, formId)
            .compose(ui -> {
                if (Objects.isNull(ui) || ui.isEmpty()) {
                    LOG.Ui.warn(LOGGER, " Field not configured.");
                    return Ux.future(new JsonArray());
                } else {
                    final JsonArray uiJson = Ut.serializeJson(ui);
                    return this.attachConfig(uiJson);
                }
            });
    }

    @Override
    public Future<JsonArray> updateA(final String controlId, final JsonArray data) {
        final ConcurrentMap<Object, Boolean> seen = new ConcurrentHashMap<>();
        // 1. mountIn fields, convert those into object from string
        final List<UiField> fields = Ut.itJArray(data)
            // filter(deduplicate) by name
            .filter(item -> (!item.containsKey(KName.NAME)) ||
                (Ut.isNotNil(item.getString(KName.NAME)) && null == seen.putIfAbsent(item.getString(KName.NAME), Boolean.TRUE)))
            .map(item -> Ut.valueToString(item,
                OPTION_JSX,
                OPTION_CONFIG,
                OPTION_ITEM,
                RULES,
                KName.METADATA
            ))
            .map(field -> field.put(KName.Ui.CONTROL_ID, Optional.ofNullable(field.getString(KName.Ui.CONTROL_ID)).orElse(controlId)))
            .map(field -> Ux.fromJson(field, UiField.class))
            .collect(Collectors.toList());
        // 2. delete old ones and insert new ones
        return this.deleteByControlId(controlId)
            .compose(result -> DB.on(UiFieldDao.class)
                .insertAsync(fields)
                .compose(Ux::futureA)
                // 3. mountOut
                .compose(Fx.ofJArray(
                    OPTION_JSX,
                    OPTION_CONFIG,
                    OPTION_ITEM,
                    RULES,
                    KName.METADATA
                )));
    }

    @Override
    public Future<Boolean> deleteByControlId(final String controlId) {
        return DB.on(UiFieldDao.class)
            .deleteByAsync(new JsonObject().put(KName.Ui.CONTROL_ID, controlId));
    }

    private Future<JsonArray> attachConfig(final JsonArray fieldJson) {
        /*
         * metadata mode for parsing processor
         */
        final JsonArray ui = new JsonArray();
        /*
         * Calculate row
         */
        final int rowIndex = Ut.itJArray(fieldJson)
            .map(each -> each.getInteger("yPoint"))
            .max(Comparator.naturalOrder())
            .orElse(0);
        for (int idx = 0; idx <= rowIndex; idx++) {
            final Integer current = idx;
            final List<JsonObject> row = Ut.itJArray(fieldJson)
                .filter(item -> current.equals(item.getInteger("yPoint")))
                .sorted(Comparator.comparing(item -> item.getInteger("xPoint")))
                .toList();
            /*
             * Calculate columns
             */
            final JsonArray rowArr = new JsonArray();
            row.forEach(cell -> {
                /*
                 * Title row is special here
                 */
                final RowType rowType = Ut.toEnum(() -> cell.getString("rowType"),
                    RowType.class, RowType.FIELD);
                final JsonObject dataCell = new JsonObject();
                if (RowType.TITLE == rowType) {
                    dataCell.put("title", cell.getValue("label"));
                    dataCell.put("field", cell.getValue(KName.KEY));    // Specific field for title.
                } else if (RowType.CONTAINER == rowType) {
                    dataCell.put("complex", Boolean.TRUE);
                    // Container type will be mapped to name field here
                    dataCell.put(KName.NAME, cell.getValue("container"));
                    // optionJsx -> config
                    Ut.valueToJObject(cell, OPTION_JSX);
                    if (Objects.nonNull(cell.getValue(OPTION_JSX))) {
                        dataCell.put(KName.Ui.CONFIG, cell.getValue(OPTION_JSX));
                    }
                } else {
                    Ut.valueToJObject(cell,
                        OPTION_JSX,
                        OPTION_CONFIG,
                        OPTION_ITEM,
                        "rules"
                    );
                    final String render = Objects.isNull(cell.getString("render")) ? "" :
                        cell.getString("render");
                    final String label = Objects.isNull(cell.getString("label")) ? "" :
                        cell.getString("label");
                    final String metadata = cell.getString("name")
                        + "," + label + "," + cell.getInteger("span")
                        + ",," + render;

                    dataCell.put("metadata", metadata);
                    /*
                     * hidden
                     */
                    final Boolean hidden = cell.getBoolean("hidden");
                    if (hidden) {
                        dataCell.put("hidden", Boolean.TRUE);
                    }
                    /*
                     * Rules
                     */
                    final JsonArray rules = cell.getJsonArray("rules");
                    if (Objects.nonNull(rules) && !rules.isEmpty()) {
                        dataCell.put("optionConfig.rules", rules);
                    }
                    /*
                     * Three core configuration
                     */
                    if (Objects.nonNull(cell.getValue(OPTION_JSX))) {
                        dataCell.put(OPTION_JSX, cell.getValue(OPTION_JSX));
                    }
                    if (Objects.nonNull(cell.getValue(OPTION_CONFIG))) {
                        dataCell.put(OPTION_CONFIG, cell.getValue(OPTION_CONFIG));
                    }
                    if (Objects.nonNull(cell.getValue(OPTION_ITEM))) {
                        dataCell.put(OPTION_ITEM, cell.getValue(OPTION_ITEM));
                    }

                    /*
                     * moment
                     * 1) When `Edit/Add` status
                     * 2) When `View` status
                     * In this kind of situation, the config `optionJsx` must contains `config.formatFail` here.
                     */
                    final JsonObject optionJsx = cell.getJsonObject(OPTION_JSX);

                    if (Ut.isNotNil(optionJsx)) {
                        final JsonObject config = optionJsx.getJsonObject("config");
                        if (Ut.isNotNil(config) && config.containsKey("formatFail")) {
                            /*
                             * Date here for moment = true
                             * Here are some difference between two components
                             * 1) For `Date`, the formatFail should be string
                             * 2) For `TableEditor`, the formatFail should be object
                             * The table editor is added new here
                             */
                            final Object format = config.getValue("formatFail");
                            if (String.class == format.getClass()) {
                                dataCell.put("moment", true);
                            }
                        }
                    }
                }
                rowArr.add(dataCell);
            });
            ui.add(rowArr);
        }
        return Ux.future(ui);
    }
}
