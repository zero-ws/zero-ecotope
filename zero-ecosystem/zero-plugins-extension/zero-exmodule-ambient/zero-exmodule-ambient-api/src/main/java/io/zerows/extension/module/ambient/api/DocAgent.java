package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * Document Management Api
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface DocAgent {
    @GET
    @Path("/document/by/directory")
    @Address(Addr.Doc.BY_DIRECTORY)
    @OpenApi
    JsonArray byDirectory(@QueryParam(KName.DIRECTORY_ID) String directoryId);

    @GET
    @Path("/document/by/keyword/:keyword")
    @Address(Addr.Doc.BY_KEYWORD)
    @OpenApi
    JsonArray byKeyword(@PathParam(KName.KEY_WORD) String keyword);

    @GET
    @Path("/document/by/trashed")
    @Address(Addr.Doc.BY_TRASHED)
    @OpenApi
    JsonArray byTrashed();


    // ----------------- Operation Api ----------------------
    /*
     * Document Management Platform
     * 1. Fetch Category by `zero.directory`.
     * 2. Capture the component of these three and call `ExIo` interface ( Service Loader )
     * 3. Create all folders based join components defined ( First Time ).
     */
    @Path("/document/start/:type")
    @GET
    @Address(Addr.Doc.DOCUMENT)
    @OpenApi
    JsonArray start(@PathParam(KName.TYPE) String type,
                    @HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    /*
     * Following Operation Api Data Structure for each is as following
     * {
     *      "key": "???",
     *      "directory": "true - DIRECTORY, false - FILE"
     * }
     */
    @Path("/document/trash")
    @DELETE
    @Address(Addr.Doc.DOCUMENT_TRASH)
    @OpenApi
    JsonArray trashIn(@BodyParam JsonArray documentA);

    @Path("/document/purge")
    @DELETE
    @Address(Addr.Doc.DOCUMENT_PURGE)
    @OpenApi
    JsonArray trashKo(@BodyParam JsonArray documentA);

    @Path("/document/rollback")
    @PUT
    @Address(Addr.Doc.DOCUMENT_ROLLBACK)
    @OpenApi
    JsonArray trashOut(@BodyParam JsonArray documentA);

    @Path("/file/rename")
    @PUT
    @Address(Addr.File.RENAME)
    @OpenApi
    JsonObject rename(@BodyParam JsonObject documentJ);

    // ----------------- File Batch Operation ----------------------
    @Path("/file/upload")
    @POST
    @Address(Addr.File.UPLOAD_CREATION)
    @OpenApi
    JsonArray upload(@BodyParam JsonArray documentA);

    @Path("/file/download")
    @POST
    @Address(Addr.File.DOWNLOADS)
    @OpenApi
    JsonArray download(@BodyParam JsonArray keys);
}
