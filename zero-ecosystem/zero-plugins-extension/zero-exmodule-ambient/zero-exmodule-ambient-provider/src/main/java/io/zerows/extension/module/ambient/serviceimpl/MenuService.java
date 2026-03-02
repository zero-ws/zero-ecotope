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
     *    1. Z_APP_ID 中会包含固定应用实例对应的ID值，此ID值作为入口应用查询的基础，不同入口应用会包含子应用列表
     *    2. 根据 APP_ID = ? 查询当前入口应用旗下所有的应用信息
     *       缓存位于 R2MO_HOME/apps/{UUID} 目录，若无 R2MO_HOME 环境变量，则默认使用当前运行目录
     *    3. 查询所有关联应用旗下的菜单信息
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
