package io.zerows.extension.commerce.erp.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.erp.domain.tables.daos.ECompanyDao;
import io.zerows.extension.commerce.erp.domain.tables.daos.EEmployeeDao;
import io.zerows.extension.commerce.erp.domain.tables.pojos.EEmployee;
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
