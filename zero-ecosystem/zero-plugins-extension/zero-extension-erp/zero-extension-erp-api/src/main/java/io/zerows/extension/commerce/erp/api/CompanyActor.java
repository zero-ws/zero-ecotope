package io.zerows.extension.commerce.erp.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.commerce.erp.common.ErpMsg;
import io.zerows.extension.commerce.erp.service.CompanyStub;
import jakarta.inject.Inject;

import static io.zerows.extension.commerce.erp.common.Er.LOG;

@Queue
public class CompanyActor {

    private static final LogOf LOGGER = LogOf.get(CompanyActor.class);

    @Inject
    private transient CompanyStub stub;

    @Address(Addr.Company.INFORMATION)
    public Future<JsonObject> company(final String employeeId) {
        LOG.Worker.info(LOGGER, ErpMsg.COMPANY_INFO, employeeId);
        return this.stub.fetchByEmployee(employeeId);
    }
}
