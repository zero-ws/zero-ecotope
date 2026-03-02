package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.daos.XMenuDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XMenu;
import io.zerows.extension.module.ambient.servicespec.MenuStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MenuService implements MenuStub {

    /**
     * 新版信息提取流程
     * <pre>
     *     1. Z_APP_ID 中会包含固定应用实例对应的ID值，此ID值作为入口应用查询的基础，不同入口应用会包含子应用列表
     *     2. 根据 APP_ID = ? 查询当前入口应用旗下所有的应用信息
     *        缓存位于 R2MO_HOME/apps/{UUID} 目录，若无 R2MO_HOME 环境变量，则默认使用当前运行目录
     *     3. 查询所有关联应用旗下的菜单信息
     * </pre>
     * 核心标识结构
     * <pre>
     *     1. 单个租户 TENANT_ID 中会包含多个 XApp 记录
     *     2. 每个应用 X_APP 中的唯一记录表示 App Instance 实例，且应用唯一标识为
     *        NAME + APP_ID
     *        CODE + APP_ID
     *     3. 环境变量 X_APP_ID 代表入口应用 ID，只有入口应用 ID 表中 ID 和 APP_ID 值相同
     *        当前方法会根据 APP_ID -> 所有 ID -> 所有菜单信息综合提取
     * </pre>
     *
     * @param appId 应用 ID
     * @return 所有合法菜单信息
     */
    @Override
    public Future<JsonArray> fetchByApp(final String appId) {
        return DB.on(XAppDao.class).<XApp>fetchAsync(KName.APP_ID, appId).compose(apps -> {
            final Set<String> ids = Ut.elementSet(apps, XApp::getId);
            if (ids.isEmpty()) {
                return Ux.futureL();
            }
            return DB.on(XMenuDao.class).<XMenu, String>fetchInAsync(KName.APP_ID, ids);
        }).compose(Ux::futureA);
    }
}
