package io.zerows.extension.crud.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Validated;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.crud.common.IxMsg;
import io.zerows.program.Ux;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.PointParam;
import jakarta.ws.rs.extension.StreamParam;

import static io.zerows.extension.crud.common.Ix.LOG;

/*
 * Export / Import file here for processing
 */
@EndPoint
@Path("/api")
public class FileAgent {

    @Path("/{actor}/import")
    @POST
    @Address(Addr.File.IMPORT)
    @Adjust(KWeb.ORDER.MODULE)
    public JsonObject importFile(@PathParam("actor") final String actor,
                                 @QueryParam(KName.MODULE) final String module,
                                 @StreamParam @Validated final FileUpload fileUpload,
                                 // For Import by different `type`
                                 @QueryParam(KName.TYPE) final String type) {
        /* File stored */
        final String filename = fileUpload.uploadedFileName();
        LOG.Dao.info(this.getClass(), IxMsg.FILE_UPLOAD, fileUpload.fileName(), filename);
        final JsonObject parameters = new JsonObject();
        return Ux.toZip(actor, filename, module, parameters.put(KName.TYPE, type));
    }

    @Path("/{actor}/export")
    @POST
    @Address(Addr.File.EXPORT)
    @Adjust(KWeb.ORDER.MODULE)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public JsonObject exportFile(@PathParam("actor") final String actor,
                                 @BodyParam final JsonObject condition,
                                 @QueryParam(KName.MODULE) final String module,
                                 @PointParam(KName.VIEW) final KView view) {
        /*
         * Toggle formatFail here
         * {
         *     "0": xxx,
         *     "1": {
         *          "columns":[],
         *          "criteria": {}
         *     },
         *     "2": "module",
         *     "3": "view"
         *     ......
         * }
         */
        return Ux.toZip(actor, condition, module, view);
    }
}
