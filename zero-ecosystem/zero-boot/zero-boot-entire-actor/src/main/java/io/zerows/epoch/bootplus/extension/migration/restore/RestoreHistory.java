package io.zerows.epoch.bootplus.extension.migration.restore;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.tookit.AbstractStatic;
import io.zerows.epoch.corpus.Ux;
import io.zerows.enums.Environment;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityChangeDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XLogDao;
import io.zerows.extension.runtime.workflow.domain.tables.daos.WTodoDao;

public class RestoreHistory extends AbstractStatic {

    public RestoreHistory(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("003.4 大表数据直接还原");
        final String folder = "history";
        return Ux.future(config)
            /* XActivityChange */
            .compose(this.restoreH(XActivityChangeDao.class, folder)::procAsync)
            /* XActivity */
            .compose(this.restoreH(XActivityDao.class, folder)::procAsync)
            /* XLog */
            .compose(this.restoreH(XLogDao.class, folder)::procAsync)
            /* WTodo */
            .compose(this.restoreT(WTodoDao.class, folder)::procAsync);
    }
}
