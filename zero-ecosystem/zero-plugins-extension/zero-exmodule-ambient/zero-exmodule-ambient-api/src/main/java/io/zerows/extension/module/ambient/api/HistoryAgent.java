package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * # Zero History Api Definition
 * <p>
 * This is interface to query history of each records from zero framework.
 * It's for `X_ACTIVITY` and `X_ACTIVITY_CHANGE` table that enabled following features.
 * <p>
 * 1. Trash to findRunning all the records that have been deleted from our system.
 * 2. Query all records' histories that have been removed.
 */
@EndPoint
@Path("/api")
public interface HistoryAgent {

    @Path("/history/{identifier}/{key}")
    @GET
    @Address(Addr.History.HISTORIES)
    @OpenApi
    Future<JsonArray> fetch(@PathParam("identifier") String identifier,
                            @PathParam("key") String key);

    @Path("/history/{identifier}/{key}/{field}")
    @GET
    @Address(Addr.History.HISTORY_BY_FIELDS)
    @OpenApi
    Future<JsonArray> fetch(@PathParam("identifier") String identifier,
                            @PathParam("key") String key,
                            @PathParam("field") String field);

    @Path("/history/{key}")
    @GET
    @Address(Addr.History.HISTORY_ITEMS)
    @OpenApi
    Future<JsonArray> fetchItems(@PathParam("key") String key);

    /*
     * The activity change histories page needed
     * Only support two read method:
     *
     * 1) Search by query engine
     * 2) Get activity by id findRunning here
     */
    @Path("/x-activity/search")
    @POST
    @Address(Addr.History.ACTIVITY_SEARCH)
    @OpenApi
    Future<JsonObject> searchActivities(@BodyParam JsonObject body);

    @Path("/x-activity/{key}")
    @GET
    @Address(Addr.History.ACTIVITY_GET)
    @OpenApi
    Future<JsonObject> fetchActivity(@PathParam("key") String key);

    /*
     * 1. Step 1: Update the Notice by `expiredAt` first
     * 2. Step 2: Query the valid `notice` records from the system
     */
    @POST
    @Path("/notice-dashboard")
    @Address(Addr.Init.NOTICE)
    @OpenApi
    JsonArray notice(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                     @BodyParam JsonObject condition);
}
