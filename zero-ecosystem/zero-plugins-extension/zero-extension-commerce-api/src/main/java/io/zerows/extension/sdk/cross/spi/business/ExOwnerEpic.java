package io.zerows.extension.sdk.cross.spi.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.commerce.erp.domain.tables.daos.ECompanyDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTenantDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XTenant;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExOwner;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;

import java.util.Objects;

/*
 * Get user information from database
 * Company Information
 */
public class ExOwnerEpic implements ExOwner {
    @Override
    public Future<JsonObject> fetchCompany(final String id) {
        return DB.on(ECompanyDao.class)
            .fetchByIdAsync(id)
            .compose(Ux::futureJ);
    }

    @Override
    public Future<JsonObject> fetchTenant(final String idOr) {
        final DBJooq jq = DB.on(XTenantDao.class);
        return jq.<XTenant>fetchByIdAsync(idOr)
            .compose(tenant -> {
                if (Objects.nonNull(tenant)) {
                    return Ux.future(tenant);
                }
                return jq.fetchOneAsync(KName.SIGMA, idOr);
            })
            .compose(Ux::futureJ);
    }

}
