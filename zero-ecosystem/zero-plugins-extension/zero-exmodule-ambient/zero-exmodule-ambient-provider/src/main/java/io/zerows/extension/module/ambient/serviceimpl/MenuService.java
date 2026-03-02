package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XMenuDao;
import io.zerows.extension.module.ambient.servicespec.MenuStub;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MenuService implements MenuStub {

    /**
     * 新版信息提取流程
     * <pre>
     *    1. Z_APP 中会包含固定应用实例
     * </pre>
     *
     * @param appId 应用 ID
     * @return 所有合法菜单信息
     */
    @Override
    public Future<JsonArray> fetchByApp(final String appId) {
        return DB.on(XMenuDao.class)
            .fetchJAsync(KName.APP_ID, appId)
            // metadata field extraction
            .map(item -> Ut.valueToJArray(item, KName.METADATA));
    }
}
