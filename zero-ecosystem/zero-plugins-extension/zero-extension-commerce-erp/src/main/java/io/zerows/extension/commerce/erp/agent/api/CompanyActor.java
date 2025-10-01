package io.zerows.extension.commerce.erp.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.epoch.common.uca.log.Annal;
import io.zerows.extension.commerce.erp.agent.service.CompanyStub;
import io.zerows.extension.commerce.erp.eon.Addr;
import io.zerows.extension.commerce.erp.eon.ErpMsg;
import jakarta.inject.Inject;

import static io.zerows.extension.commerce.erp.util.Er.LOG;

@Queue
public class CompanyActor {

    private static final Annal LOGGER = Annal.get(CompanyActor.class);

    @Inject
    private transient CompanyStub stub;

    @Address(Addr.Company.INFORMATION)
    public Future<JsonObject> company(final String employeeId) {
        LOG.Worker.info(LOGGER, ErpMsg.COMPANY_INFO, employeeId);
        return this.stub.fetchByEmployee(employeeId);
    }
}
