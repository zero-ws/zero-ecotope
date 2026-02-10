package io.zerows.extension.module.mbseapi.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;

@Path("/api")
@EndPoint
public interface TaskApi {
    @Path("/job/start/{code}")
    @PUT
    @Address(JtAddr.Job.START)
    @OpenApi
    String startJob(@PathParam("code") String code);

    @Path("/job/stop/{code}")
    @PUT
    @Address(JtAddr.Job.STOP)
    @OpenApi
    String stopJob(@PathParam("code") String code);

    @Path("/job/resume/{code}")
    @PUT
    @Address(JtAddr.Job.RESUME)
    @OpenApi
    String resumeJob(@PathParam("code") String code);

    @Path("/job/info/status/{namespace}")
    @GET
    @Address(JtAddr.Job.STATUS)
    @OpenApi
    String statusJob(@PathParam("namespace") String namespace);

    @Path("/job/info/by/sigma")
    @POST
    @Address(JtAddr.Job.BY_SIGMA)
    @OpenApi
    String fetchJobs(@HeaderParam(KWeb.HEADER.X_SIGMA) String sigma,
                     @BodyParam JsonObject body,
                     @QueryParam("group") @DefaultValue("false") Boolean grouped);

    @Path("/job/info/mission/:key")
    @GET
    @Address(JtAddr.Job.GET_BY_KEY)
    @OpenApi
    String fetchJob(@PathParam("key") String key);

    @Path("/job/info/mission/:key")
    @PUT
    @Address(JtAddr.Job.UPDATE_BY_KEY)
    @OpenApi
    String updateJob(@PathParam("key") String key,
                     @BodyParam JsonObject data);

}
