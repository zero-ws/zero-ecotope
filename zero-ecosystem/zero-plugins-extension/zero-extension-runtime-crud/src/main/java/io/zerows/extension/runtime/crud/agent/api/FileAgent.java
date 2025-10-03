package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.epoch.corpus.io.annotations.PointParam;
import io.zerows.epoch.corpus.io.annotations.StreamParam;
import io.zerows.epoch.corpus.metadata.commune.Vis;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.IxMsg;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

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
                                 @StreamParam @Codex final FileUpload fileUpload,
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
                                 @PointParam(KName.VIEW) final Vis view) {
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
