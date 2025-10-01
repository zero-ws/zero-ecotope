package io.mature.extension.migration.backup;

import io.mature.extension.migration.tookit.AbstractStatic;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.Environment;
import io.zerows.extension.commerce.rbac.domain.tables.daos.OUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.unity.Ux;

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
