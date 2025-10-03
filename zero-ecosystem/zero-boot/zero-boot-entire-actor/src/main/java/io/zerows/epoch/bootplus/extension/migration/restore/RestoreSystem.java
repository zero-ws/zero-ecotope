package io.zerows.epoch.bootplus.extension.migration.restore;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.tookit.AbstractStatic;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XMenuDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XModuleDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XNumberDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTabularDao;
import io.zerows.platform.enums.Environment;
import io.zerows.program.Ux;

public class RestoreSystem extends AbstractStatic {

    public RestoreSystem(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("003.3 系统数据还原");
        final String folder = "system";
        return Ux.future(config)
            /* XApp */
            .compose(this.restoreT(XAppDao.class, folder)::procAsync)
            /* XCategory */
            .compose(this.restoreT(XCategoryDao.class, folder)::procAsync)
            /* XNumber */
            .compose(this.restoreT(XNumberDao.class, folder)::procAsync)
            /* XMenu */
            .compose(this.restoreT(XMenuDao.class, folder)::procAsync)
            /* XTabular */
            .compose(this.restoreT(XTabularDao.class, folder)::procAsync)
            /* XModule */
            .compose(this.restoreT(XModuleDao.class, folder)::procAsync)
            /* XSource */
            .compose(this.restoreT(XSourceDao.class, folder)::procAsync);
    }
}
