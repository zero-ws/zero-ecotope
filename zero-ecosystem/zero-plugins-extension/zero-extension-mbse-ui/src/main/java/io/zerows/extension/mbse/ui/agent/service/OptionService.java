package io.zerows.extension.mbse.ui.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import io.zerows.extension.mbse.ui.domain.tables.daos.*;
import io.zerows.extension.mbse.ui.domain.tables.pojos.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class OptionService implements OptionStub {
    @Override
    public Future<JsonObject> fetchQuery(final String id) {
        return DB.on(VQueryDao.class)
            .<VQuery>fetchByIdAsync(id)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(
                FIELD_QUERY_CRITERIA,
                FIELD_QUERY_PROJECTION
            ));
    }

    @Override
    public Future<JsonObject> fetchSearch(final String id) {
        return DB.on(VSearchDao.class)
            .<VSearch>fetchByIdAsync(id)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(
                FIELD_SEARCH_NOTICE,
                FIELD_SEARCH_VIEW,
                FIELD_SEARCH_COND
            ));
    }

    @Override
    public Future<JsonObject> fetchFragment(final String id) {
        return DB.on(VFragmentDao.class)
            .<VFragment>fetchByIdAsync(id)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(
                FIELD_FRAGMENT_MODEL,
                FIELD_FRAGMENT_NOTICE,
                FIELD_FRAGMENT_CONFIG,
                FIELD_FRAGMENT_BUTTON_GROUP
            ));
    }

    @Override
    public Future<JsonObject> fetchTable(final String id) {
        return DB.on(VTableDao.class)
            .<VTable>fetchByIdAsync(id)
            .compose(Ux::futureJ)
            .compose(Fx.ofJObject(
                FIELD_TABLE_OP_CONFIG
            ));
    }

    @Override
    public Future<JsonArray> updateA(final String controlId, final JsonArray data) {
        final ConcurrentMap<Object, Boolean> seen = new ConcurrentHashMap<>();
        // 1. mountIn fields, convert those into object from string
        final List<UiOp> ops = Ut.itJArray(data)
            // filter(deduplicate) by action
            .filter(item -> Ut.isNotNil(item.getString("action")) && null == seen.putIfAbsent(item.getString("action"), Boolean.TRUE))
            .map(item -> Ut.valueToString(item,
                FIELD_OP_CONFIG,
                FIELD_OP_PLUGIN,
                KName.METADATA
            ))
            .map(field -> field.put(KName.Ui.CONTROL_ID, Optional.ofNullable(field.getString(KName.Ui.CONTROL_ID)).orElse(controlId)))
            .map(field -> Ux.fromJson(field, UiOp.class))
            .collect(Collectors.toList());
        // 2. delete old ones and insert new ones
        return this.deleteByControlId(controlId)
            .compose(result -> DB.on(UiOpDao.class)
                .insertAsync(ops)
                .compose(Ux::futureA)
                // 3. mountOut
                .compose(Fx.ofJArray(
                    FIELD_OP_CONFIG,
                    FIELD_OP_PLUGIN,
                    KName.METADATA
                )));
    }

    @Override
    public Future<Boolean> deleteByControlId(final String controlId) {
        return DB.on(UiOpDao.class)
            .deleteByAsync(new JsonObject().put(KName.Ui.CONTROL_ID, controlId));
    }
}
