package io.zerows.extension.module.mbseapi.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IApi;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IJob;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;

/*
 * Interface here for
 * 1) Update job by `JtJob`
 * -- ServiceEnvironment updating
 *
 */
public interface AmbientStub {
    /*
     * Job information updating by `AmbientStub`
     */
    Future<JsonObject> updateJob(IJob job, IService service);

    /*
     * Uri information updating by `AmbientStub`
     */
    Future<JsonObject> updateUri(IApi api, IService service);
}
