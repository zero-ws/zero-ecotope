package io.zerows.epoch.bootplus.extension.migration.backup;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.tookit.AbstractStatic;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.enums.Environment;
import io.zerows.extension.commerce.rbac.domain.tables.daos.OUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;

public class BackupUser extends AbstractStatic {

    public BackupUser(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("003.12. 备份账号信息");

        final String folder = "user";
        final JsonObject configNo = config.copy();
        configNo.put("all", Boolean.TRUE);
        return Ux.future(config)
            /* SUser */
            .compose(this.backupT(SUserDao.class, folder)::procAsync)
            /* SRole */
            .compose(this.backupT(SRoleDao.class, folder)::procAsync)
            .compose(proccesed -> Ux.future(configNo))
            /* OUser */
            .compose(this.backupT(OUserDao.class, folder)::procAsync)
            /* RUserRole */
            .compose(this.backupT(RUserRoleDao.class, folder)::procAsync);
    }
}
