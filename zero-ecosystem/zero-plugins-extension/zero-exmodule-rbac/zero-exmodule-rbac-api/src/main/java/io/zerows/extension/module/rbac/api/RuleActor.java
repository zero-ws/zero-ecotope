package io.zerows.extension.module.rbac.api;

import io.r2mo.base.dbe.syntax.QSorter;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScOwner;
import io.zerows.extension.module.rbac.domain.tables.daos.SPathDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPath;
import io.zerows.extension.module.rbac.servicespec.RuleStub;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class RuleActor {

    @Inject
    private transient RuleStub stub;

    @Address(Addr.Rule.FETCH_REGION)
    public Future<JsonArray> fetchRegions(final String type, final XHeader header) {
        // RUN_TYPE = ? AND SIGMA = ?
        final JsonObject condition = Ux.whereAnd()
            .put(KName.RUN_TYPE, type)
            .put(KName.SIGMA, header.getSigma())
            .put(KName.PARENT_ID + ",n", VString.EMPTY);

        return DB.on(SPathDao.class)
            .<SPath>fetchAsync(condition, QSorter.of(KName.UI_SORT, true))
            .compose(Ux::futureA);
    }

    @Address(Addr.Rule.FETCH_REGION_DEFINE)
    public Future<JsonObject> fetchRegion(final String key) {
        return DB.on(SPathDao.class)
            .<SPath>fetchByIdAsync(key)
            .compose(this.stub::regionAsync);
    }

    /*
     * 最终响应的数据格式根据 iConfig 中的定义执行
     */
    @Address(Addr.Rule.FETCH_REGION_VALUES)
    public Future<JsonObject> fetchValues(final String ownerId, final JsonObject pathJ,
                                          final KView view) {
        final ScOwner owner = new ScOwner(ownerId, pathJ.getString(KName.RUN_TYPE));
        owner.bind(view);
        return this.stub.regionAsync(pathJ, owner);
    }


    @Me
    @Address(Addr.Rule.SAVE_REGION)
    public Future<JsonObject> saveRegion(final String pathCode, final JsonObject viewData,
                                         final User user) {
        /*
         * {
         *     "owner":         ??,
         *     "ownerType":     ??,
         *     "name":          ??,
         *     "position":      ??,
         *     "active":        true,
         *     "language":      cn,
         *     "sigma":         xxx,
         *     "resource": {
         *         "code1": {
         *              "rows":          ??,
         *              "projection":    ??,
         *              "criteria":      ??,
         *              "view":          ??,
         *              "position":      ??
         *         }
         *     }
         * }
         * 转换（多视图存储转换）
         * {
         *     "resource-code1": [],
         *     "resource-code2": []
         * }
         * -- 追加 owner, ownerType, ( view -> name ), position, updatedBy
         * 返回数据：
         * {
         *     v, h, q,
         *     virtual,
         *     visitant: {
         *         seekKey1: {},
         *         seekKey2: {}
         *     }
         * }
         */
        final String userKey = Ux.userId(user);
        // CODE = ? AND SIGMA = ?
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.SIGMA, Ut.valueString(viewData, KName.SIGMA));
        condition.put(KName.CODE, pathCode);
        return this.stub.regionAsync(condition, this.valueBody(viewData, userKey));
    }

    private JsonObject valueBody(final JsonObject viewData, final String userKey) {
        final JsonObject resourceBody = new JsonObject();
        final JsonObject resourceI = Ut.valueJObject(viewData, KName.RESOURCE);
        /*
         * Normalize Data Processing
         * 1) Update Directly
         * 2) Update Visitant ( Deeply Saving )
         */
        resourceI.fieldNames().forEach(resourceKey -> {
            final JsonArray viewQueue = new JsonArray();
            final Object view = resourceI.getValue(resourceKey);
            if (view instanceof final JsonObject viewJ) {
                viewQueue.add(this.valueView(viewData, userKey, viewJ));
            } else if (view instanceof final JsonArray viewA) {
                Ut.itJArray(viewA).map(eachJ -> this.valueView(viewData, userKey, eachJ)).forEach(viewQueue::add);
            }
            resourceBody.put(resourceKey, viewQueue);
        });
        return resourceBody;
    }

    private JsonObject valueView(final JsonObject viewData, final String userKey, final JsonObject eachJ) {
        final JsonObject resourceJ = eachJ.copy();
        Ut.valueCopy(resourceJ, viewData,
            KName.SIGMA, KName.LANGUAGE, KName.OWNER, KName.OWNER_TYPE
        );
        resourceJ.put(KName.NAME, resourceJ.getValue(KName.VIEW));
        resourceJ.put(KName.UPDATED_BY, userKey);
        return resourceJ;
    }
}
