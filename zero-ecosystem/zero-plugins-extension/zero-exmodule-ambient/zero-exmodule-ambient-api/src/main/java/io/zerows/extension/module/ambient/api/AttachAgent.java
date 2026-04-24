package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.boot.AtConfig;
import io.zerows.extension.module.ambient.boot.MDAmbientManager;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.skeleton.common.enums.FileStatus;
import io.zerows.support.Ut;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.StreamParam;

import java.text.MessageFormat;
import java.util.UUID;

/*
 * Uniform attachment upload/download
 */
@EndPoint
@Path("/api")
public class AttachAgent {

    private static final MDAmbientManager MANAGER = MDAmbientManager.of();

    @Path("/file/upload/{identifier}")
    @POST
    @Address(Addr.File.UPLOAD)
    @OpenApi
    public JsonObject upload(@PathParam(KName.IDENTIFIER) final String identifier,
                             @QueryParam(KName.CATEGORY) final String category,
                             @QueryParam(KName.DIRECTORY) final String directory,
                             @StreamParam final FileUpload fileUpload) {
        final JsonObject uploaded = new JsonObject();
        final String originalFile = fileUpload.fileName();
        if (Ut.isNotNil(originalFile) && originalFile.contains(".")) {
            final AtConfig config = MANAGER.config();
            final int lastIndex = originalFile.lastIndexOf('.');
            final String fileName = originalFile.substring(0, lastIndex);
            final String extension = originalFile.substring(lastIndex + 1);
            final String key = UUID.randomUUID().toString();
            final String downloadUrl = MessageFormat.format(AtConstant.DOWNLOAD_URI, key);
            uploaded.put(KName.KEY, key)
                .put(KName.STATUS, FileStatus.DONE.name())
                .put(KName.TYPE, fileUpload.contentType())
                .put(KName.MIME, fileUpload.contentType())
                .put(KName.NAME, originalFile)
                .put(KName.FILE_KEY, Ut.randomString(64))
                .put(KName.Attachment.FILE_NAME, fileName)
                .put(KName.EXTENSION, extension)
                .put(KName.SIZE, fileUpload.size())
                .put(KName.Attachment.FILE_URL, downloadUrl)
                .put(KName.Attachment.FILE_PATH, fileUpload.uploadedFileName())
                .put(KName.MODEL_ID, identifier)
                .put(KName.MODEL_CATEGORY, category)
                .put(KName.LANGUAGE, config.getFileLanguage())
                .put(KName.METADATA, new JsonObject().encode())
                .put(KName.Attachment.STORE_WAY, config.getFileStorage())
                .put(KName.DIRECTORY, directory);
        }
        return uploaded;
    }

    @Path("/file/upload/session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Address(Addr.File.UPLOAD_SESSION_INIT)
    @OpenApi
    public JsonObject initSession(@BodyParam final JsonObject body) {
        return body;
    }

    @Path("/file/upload/session/{token}")
    @GET
    @Address(Addr.File.UPLOAD_SESSION_STATUS)
    @OpenApi
    public String sessionStatus(@PathParam("token") final String token) {
        return token;
    }

    @Path("/file/upload/session/{token}/chunk/{index}")
    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Address(Addr.File.UPLOAD_SESSION_CHUNK)
    @OpenApi
    public JsonObject uploadChunk(@PathParam("token") final String token,
                                  @PathParam("index") final Integer index,
                                  @BodyParam final Buffer body) {
        return new JsonObject().put("token", token).put("index", index).put("size", body.length());
    }

    @Path("/file/upload/session/{token}/complete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Address(Addr.File.UPLOAD_SESSION_COMPLETE)
    @OpenApi
    public String completeSession(@PathParam("token") final String token,
                                  @BodyParam final JsonObject body) {
        return token;
    }

    @Path("/file/upload/session/{token}")
    @DELETE
    @Address(Addr.File.UPLOAD_SESSION_CANCEL)
    @OpenApi
    public String cancelSession(@PathParam("token") final String token) {
        return token;
    }

    @Path("/file/download/{fileKey}")
    @GET
    @Address(Addr.File.DOWNLOAD)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @OpenApi
    public JsonObject download(@PathParam("fileKey") final String key,
                               @HeaderParam("Range") final String range) {
        final JsonObject filters = new JsonObject().put(KName.KEY, key);
        if (Ut.isNotNil(range)) {
            filters.put("Range", range);
        }
        return filters;
    }
}
