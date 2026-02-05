package io.zerows.extension.crud.api;

import io.r2mo.openapi.operations.DescCrud;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Validated;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.program.Ux;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.PointParam;
import jakarta.ws.rs.extension.StreamParam;
import lombok.extern.slf4j.Slf4j;

/*
 * Export / Import file here for processing
 */
@EndPoint
@Path("/api")
@Slf4j
@Tag(name = DescCrud.group, description = DescCrud.description)
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
        log.info("{} 文件信息，文件名 {} / 上传名 = {}",
            IxConstant.K_PREFIX, fileUpload.fileName(), filename);
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
