package io.zerows.extension.module.rbac.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Validated;
import io.zerows.program.Ux;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.StreamParam;

import static io.zerows.extension.module.rbac.boot.Sc.LOG;

/*
 * User
 * 1）Import here for processing, this import will overwrite
 * - /api/user/import of uri
 * 2) It will import multi roles formatFail such as
 * - A,B,D
 */
@EndPoint
@Path("/api")
public class FileAgent {

    @Path("/user/import")
    @POST
    @Address(Addr.User.IMPORT)
    public JsonObject importUser(@StreamParam @Validated final FileUpload fileUpload) {
        /* File stored */
        final String filename = fileUpload.uploadedFileName();
        LOG.Web.info(this.getClass(), "User importing, filename = `{0}`, uploaded = `{1}`", fileUpload.fileName(), filename);
        return Ux.toZip(filename);
    }
}
