package io.zerows.extension.module.rbac.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Validated;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.program.Ux;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.StreamParam;
import lombok.extern.slf4j.Slf4j;

/*
 * User
 * 1）Import here for processing, this import will overwrite
 * - /api/user/import of uri
 * 2) It will import multi roles formatFail such as
 * - A,B,D
 */
@EndPoint
@Path("/api")
@Slf4j
public class FileAgent {

    @Path("/user/import")
    @POST
    @Address(Addr.User.IMPORT)
    @OpenApi
    public JsonObject importUser(@StreamParam @Validated final FileUpload fileUpload) {
        /* File stored */
        final String filename = fileUpload.uploadedFileName();
        log.info("{} 用户导入 | 文件名：{} / 上传文件：{}", ScConstant.K_PREFIX, fileUpload.fileName(), filename);
        return Ux.toZip(filename);
    }
}
