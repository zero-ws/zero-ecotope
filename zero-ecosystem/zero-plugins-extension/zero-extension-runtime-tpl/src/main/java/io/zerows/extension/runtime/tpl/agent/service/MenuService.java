package io.zerows.extension.runtime.tpl.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.runtime.tpl.domain.tables.daos.MyMenuDao;
import io.zerows.extension.runtime.tpl.domain.tables.pojos.MyMenu;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;

import java.util.List;

import static io.zerows.extension.runtime.tpl.util.Tl.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MenuService implements MenuStub {

    @Override
    public Future<JsonArray> fetchMy(final JsonObject condition) {
        condition.put(VString.EMPTY, Boolean.TRUE);
        LOG.Qr.info(this.getClass(), "My menu condition: {0}", condition.encode());
        return Ux.Jooq.on(MyMenuDao.class).fetchJAsync(condition);
    }

    /*
     * {
     *      "owner": "xxx",
     *      "page": "",
     *      "position": "",
     *      "type": ""
     *      "menus": [
     *      ]
     * }
     */
    @Override
    public Future<JsonArray> saveMy(final JsonObject condition, final JsonArray data) {
        LOG.Qr.info(this.getClass(), "My menu saving: {0}, data = {1}",
            condition.encode(), data.encode());
        final DBJooq jooq = Ux.Jooq.on(MyMenuDao.class);
        return jooq.deleteByAsync(condition).compose(removed -> {
            final List<MyMenu> menus = Ux.fromJson(data, MyMenu.class);
            return jooq.insertJAsync(menus);
        });
    }
}
