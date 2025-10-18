package io.zerows.extension.mbse.ui.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.component.qr.Sorter;
import io.zerows.component.qr.syntax.Ir;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.ui.domain.tables.daos.UiListDao;
import io.zerows.extension.mbse.ui.domain.tables.daos.UiViewDao;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiList;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiView;
import io.zerows.extension.mbse.ui.uca.qbe.QBECache;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

public class ListService implements ListStub {
    private static final LogOf LOGGER = LogOf.get(ListService.class);
    @Inject
    private transient OptionStub optionStub;

    @Override
    public Future<JsonObject> fetchById(final String listId) {
        /*
         * Read list configuration for configuration
         */
        return DB.on(UiListDao.class).<UiList>fetchByIdAsync(listId).compose(list -> {
            if (Objects.isNull(list)) {
                LOG.Ui.warn(LOGGER, " Form not found, id = {0}", listId);
                return Ux.future(new JsonObject());
            } else {
                /*
                 * It means here are some additional configuration that should be
                 * fetch then
                 */
                final JsonObject listJson = Ut.serializeJson(list);
                return this.attachConfig(listJson);
            }
        });
    }

    @Override
    public Future<JsonArray> fetchByIdentifier(final String identifier, final String sigma) {
        final JsonObject condition = new JsonObject();
        condition.put(KName.IDENTIFIER, identifier);
        condition.put(KName.SIGMA, sigma);
        return DB.on(UiListDao.class).<UiList>fetchAndAsync(condition)
            /* List<UiList> */
            .compose(Ux::futureA);
    }

    @Override
    public Future<JsonArray> fetchQr(final JsonObject condition) {

        final Sorter sorter = Sorter.create(KName.SORT, true);

        return DB.on(UiViewDao.class).<UiView>fetchAsync(condition, sorter)
            // Cached Data for future usage
            .compose(QBECache::cached)
            .compose(Ux::futureA)
            /* List<UiListQr> */
            .compose(Ux.futureF(
                /*
                 * 1. 标准：criteria, projection, rows
                 * 2. 扩展：qrComponent, qrConfig
                 * 3. 安全：view, position
                 * 上述七个字段不出现在返回列表中，在执行Qr时做后端运算，请求时只提供
                 * 当前Qr的名称, Qr存储的名字使用标准的 position / view 的模式，传入时
                 * 执行 Base64 加密，后端可直接解密操作
                 */
                Ir.KEY_CRITERIA, Ir.KEY_PROJECTION, KName.Rbac.ROWS,
                KName.Component.QR_COMPONENT, KName.Component.QR_CONFIG,
                KName.VIEW, KName.POSITION
            ));
    }

    private Future<JsonObject> attachConfig(final JsonObject listJson) {
        /*
         * Capture important configuration here
         */
        Ut.valueToJObject(listJson,
            ListStub.FIELD_OPTIONS,
            ListStub.FIELD_OPTIONS_AJAX,
            ListStub.FIELD_OPTIONS_SUBMIT,
            ListStub.FIELD_V_SEGMENT
        );
        return Ux.future(listJson)
            /* vQuery */
            .compose(Fx.ofJObject(ListStub.FIELD_V_QUERY, this.optionStub::fetchQuery))
            /* vSearch */
            .compose(Fx.ofJObject(ListStub.FIELD_V_SEARCH, this.optionStub::fetchSearch))
            /* vTable */
            .compose(Fx.ofJObject(ListStub.FIELD_V_TABLE, this.optionStub::fetchTable))
            /* vSegment */
            .compose(Fx.ofTree(ListStub.FIELD_V_SEGMENT, this.optionStub::fetchFragment))
            /* Combiner for final processing */
            .compose(Fx.ofWebUi("classCombiner"));
    }
}
