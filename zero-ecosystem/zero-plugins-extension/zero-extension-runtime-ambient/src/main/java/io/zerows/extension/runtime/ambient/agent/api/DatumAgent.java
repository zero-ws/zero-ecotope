package io.zerows.extension.runtime.ambient.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.*;

/**
 * 此处为消费接口，最新版直接追加
 * <pre><code>
 *     SQL:         ACTIVE = TRUE
 *     JSON:        active = true
 * </code></pre>
 * 的查询条件来对数据字典进行消费筛选（此处已追加）
 */
@EndPoint
@Path("/api")
public interface DatumAgent {

    @Path("/type/categories/{type}")
    @GET
    @Address(Addr.Datum.CATEGORY_TYPE)
    JsonArray categoryByType(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                             @PathParam("type") String type,
                             @QueryParam("leaf") @DefaultValue("true") Boolean includeLeaf);

    @Path("/types/categories")
    @POST
    @Address(Addr.Datum.CATEGORY_TYPES)
    JsonObject fetchCategories(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                               @BodyParam JsonArray types);

    @Path("/{type}/category/{code}")
    @GET
    @Address(Addr.Datum.CATEGORY_CODE)
    JsonArray fetchCategory(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                            @PathParam("type") String type,
                            @PathParam("code") String code);

    @Path("/type/tabulars/{type}")
    @GET
    @Address(Addr.Datum.TABULAR_TYPE)
    JsonArray tabularByType(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                            @PathParam("type") String type);

    @Path("/types/tabulars")
    @POST
    @Address(Addr.Datum.TABULAR_TYPES)
    JsonObject fetchTabulars(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                             @BodyParam JsonArray types);

    @Path("/{type}/tabular/{code}")
    @GET
    @Address(Addr.Datum.TABULAR_CODE)
    JsonArray fetchTabular(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId,
                           @PathParam("type") String type,
                           @PathParam("code") String code);

}
