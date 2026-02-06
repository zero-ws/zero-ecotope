package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface LinkAgent {
    /*
     * Fetch all linkages by
     * {
     *      "sigma": "identify application",
     *      "type": "the type means the category of linkage in current system, Fixed"
     * }
     */
    @GET
    @Path("/linkage/type/:type")
    @Address(Addr.Linkage.FETCH_BY_TYPE)
    @OpenApi
    JsonArray fetchByType(@PathParam(KName.TYPE) String type);

    /*
     * Fetch all by
     * - sourceKey
     */
    @GET
    @Path("/linkage/v/source/:key")
    @Address(Addr.Linkage.FETCH_TARGET)
    @OpenApi
    JsonArray fetchTarget(@PathParam(KName.KEY) String key);

    /*
     * Fetch all by
     * - targetKey
     */
    @GET
    @Path("/linkage/v/ofMain/:key")
    @Address(Addr.Linkage.FETCH_SOURCE)
    @OpenApi
    JsonArray fetchSource(@PathParam(KName.KEY) String key);

    /*
     * Fetch all by
     * - key sourceKey or targetKey
     */
    @GET
    @Path("/linkage/b/:key")
    @Address(Addr.Linkage.FETCH_ST)
    @OpenApi
    JsonArray fetchSt(@PathParam(KName.KEY) String key);

    @POST
    @Path("/linkage/b/batch/save")
    @Address(Addr.Linkage.SAVE_BATCH_B)
    @OpenApi
    JsonArray batchSaveB(@BodyParam JsonArray data);

    @POST
    @Path("/linkage/v/batch/save")
    @Address(Addr.Linkage.SAVE_BATCH_V)
    @OpenApi
    JsonArray batchSaveV(@BodyParam JsonArray data);

    /*
     * Three part of following:
     * {
     *     removed: []
     *     data: []
     * }
     * - removed: key set of removed linkage, the key is X_LINKAGE primary key
     * - data: JsonArray data of linkage that should be stored.
     */
    @POST
    @Path("/linkage/sync/b")
    @Address(Addr.Linkage.SYNC_B)
    @OpenApi
    JsonArray syncB(@BodyParam JsonObject request);

    // ----------------- Spec for CRUD
    @POST
    @Path("/linkage/b/:type")
    @Address(Addr.Linkage.ADD_NEW_B)
    @OpenApi
    JsonObject createB(@PathParam(KName.TYPE) String type,
                       @BodyParam JsonObject body);

    @POST
    @Path("/linkage/v/:type")
    @Address(Addr.Linkage.ADD_NEW_V)
    @OpenApi
    JsonObject createV(@PathParam(KName.TYPE) String type,
                       @BodyParam JsonObject body);

    @GET
    @Path("/linkage/:key")
    @Address(Addr.Linkage.FETCH_BY_KEY)
    @OpenApi
    JsonObject fetch(@PathParam(KName.KEY) String key);

    @DELETE
    @Path("/linkage/:key")
    @Address(Addr.Linkage.REMOVE_BY_REGION)
    @OpenApi
    JsonObject remove(@PathParam(KName.KEY) String key);
}
