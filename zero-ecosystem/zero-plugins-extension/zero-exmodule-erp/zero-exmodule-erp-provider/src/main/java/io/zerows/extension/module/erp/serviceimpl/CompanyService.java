package io.zerows.extension.module.erp.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.erp.domain.tables.daos.ECompanyDao;
import io.zerows.extension.module.erp.domain.tables.daos.EEmployeeDao;
import io.zerows.extension.module.erp.domain.tables.pojos.EEmployee;
import io.zerows.extension.module.erp.servicespec.CompanyStub;
import io.zerows.program.Ux;

import java.util.Objects;

public class CompanyService implements CompanyStub {
    @Override
    public Future<JsonObject> fetchByEmployee(final String employeeId) {
        return DB.on(EEmployeeDao.class)
            .<EEmployee>fetchByIdAsync(employeeId)
            .compose(employee -> DB.on(ECompanyDao.class)
                .fetchByIdAsync(Objects.isNull(employee) ?
                    null : employee.getCompanyId()))
            .compose(Ux::futureJ);
    }
}
