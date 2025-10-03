package io.zerows.epoch.bootplus.extension.migration.backup;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.tookit.AbstractStatic;
import io.zerows.epoch.corpus.Ux;
import io.zerows.platform.enums.Environment;
import io.zerows.extension.commerce.erp.domain.tables.daos.*;

public class BackupOrg extends AbstractStatic {

    public BackupOrg(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {

        this.banner("003.11. 备份组织架构");
        final String folder = "org";
        return Ux.future(config)
            /* ECompany */
            .compose(this.backupT(ECompanyDao.class, folder)::procAsync)
            /* EDept */
            .compose(this.backupT(EDeptDao.class, folder)::procAsync)
            /* ETeam */
            .compose(this.backupT(ETeamDao.class, folder)::procAsync)
            /* ECustomer */
            .compose(this.backupT(ECustomerDao.class, folder)::procAsync)
            /* EIdentity */
            .compose(this.backupT(EIdentityDao.class, folder)::procAsync)
            /* EEmployee */
            .compose(this.backupT(EEmployeeDao.class, folder)::procAsync)
            .compose(nil -> Ux.future(config));
    }
}
