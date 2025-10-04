package io.zerows.extension.runtime.ambient.agent.api.file;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.ambient.bootstrap.AtConfig;
import io.zerows.extension.runtime.ambient.bootstrap.AtPin;
import io.zerows.extension.runtime.ambient.eon.Addr;
import io.zerows.extension.runtime.ambient.eon.AtConstant;
import io.zerows.extension.runtime.skeleton.eon.em.FileStatus;
import io.zerows.support.Ut;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.StreamParam;

import java.text.MessageFormat;
import java.util.UUID;

/*
 * Uniform attachment upload/download
 */
@EndPoint
@Path("/api")
public class AttachAgent {

    @Path("/file/upload/{identifier}")
    @POST
    @Address(Addr.File.UPLOAD)
    public JsonObject upload(@PathParam(KName.IDENTIFIER) final String identifier,
                             @QueryParam(KName.CATEGORY) final String category,
                             @QueryParam(KName.DIRECTORY) final String directory,
                             @StreamParam final FileUpload fileUpload) {
        final JsonObject uploaded = new JsonObject();
        final String originalFile = fileUpload.fileName();
        if (Ut.isNotNil(originalFile) && originalFile.contains(".")) {
            // Config Read
            final AtConfig config = AtPin.getConfig();
            final int lastIndex = originalFile.lastIndexOf('.');
            final String fileName = originalFile.substring(0, lastIndex);
            final String extension = originalFile.substring(lastIndex + 1);
            // File key
            final String key = UUID.randomUUID().toString();
            // File Url
            final String downloadUrl = MessageFormat.format(AtConstant.DOWNLOAD_URI, key);
            uploaded.put(KName.KEY, key)                                        // The primary key of attachment
                // New workflow for uploading, the default status is DONE
                .put(KName.STATUS, FileStatus.DONE.name())                      // File Status: PROGRESS, DONE
                .put(KName.TYPE, fileUpload.contentType())                      // (Reserved)
                .put(KName.MIME, fileUpload.contentType())                      // MIME type here
                .put(KName.NAME, originalFile)                                  // File name: name.extension
                .put(KName.FILE_KEY, Ut.randomString(64))                // File Key that has been generated
                .put(KName.Attachment.FILE_NAME, fileName)                      // File name without extension: name
                .put(KName.EXTENSION, extension)                                // File extension name
                .put(KName.SIZE, fileUpload.size())                             // File size
                .put(KName.Attachment.FILE_URL, downloadUrl)                    // Download Url for user download
                .put(KName.Attachment.FILE_PATH, fileUpload.uploadedFileName()) // Stored file path, schedule remove all invalid files based on this field
                .put(KName.MODEL_ID, identifier)                                // Related Model Identifier
                .put(KName.MODEL_CATEGORY, category)                            // Related Model field dim for different category
                .put(KName.LANGUAGE, config.getFileLanguage())                  // Configured System Language
                .put(KName.METADATA, new JsonObject().encode())                 // (Reserved)

                .put(KName.Attachment.STORE_WAY, config.getFileStorage())       // 「Dir」Configured Stored Way
                .put(KName.DIRECTORY, directory);                               // 「Dir」Will be calculate
            // Here only left `modelKey` field.
        }
        return uploaded;
    }

    @Path("/file/download/{fileKey}")
    @GET
    @Address(Addr.File.DOWNLOAD)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public JsonObject download(@PathParam("fileKey") final String key) {
        return new JsonObject().put(KName.KEY, key);
    }
}
