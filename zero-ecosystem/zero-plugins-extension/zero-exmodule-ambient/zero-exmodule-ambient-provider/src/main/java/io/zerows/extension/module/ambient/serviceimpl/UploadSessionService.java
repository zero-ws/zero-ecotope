package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.HTransferParam;
import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.common.RFS;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.spi.FactoryIoCommon;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.module.ambient.boot.AtConfig;
import io.zerows.extension.module.ambient.boot.MDAmbientManager;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.servicespec.UploadStub;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class UploadSessionService implements UploadStub {

    private static final String CACHE_NAME = "ambient.upload.session";
    private static final long CACHE_TTL_SECONDS = 24 * 60 * 60;
    private static final String META_TOKEN = "transferToken";
    private static final String META_FINAL_PATH = "finalPath";
    private static final String META_FILE_NAME = "fileName";
    private static final String META_TOTAL_SIZE = "totalSize";
    private static final String META_CHUNK_SIZE = "chunkSize";
    private static final String META_CHUNK_COUNT = "chunkCount";
    private static final String META_IDENTIFIER = "identifier";
    private static final String META_CATEGORY = "category";
    private static final String META_DIRECTORY = "directory";
    private static final String META_MIME = "mime";
    private static final String META_CHECKSUM = "checksum";
    private static final String META_APP_ID = "appId";
    private static final String META_SIGMA = "sigma";
    private static final String META_TENANT_ID = "tenantId";
    private static final String META_UPDATED_BY = "updatedBy";
    private static final String META_CREATED_AT = "createdAt";
    static final String META_CONTEXT = "ambientUploadContext";

    private final HMM<String, JsonObject> cache = HMM.of(CACHE_NAME);
    private final HmmTokenPool tokenPool;
    private final RFS rfs;
    private final UploadAttachmentBridge bridge;
    private final MDAmbientManager manager = MDAmbientManager.of();

    public UploadSessionService() {
        final HTransfer transfer = new FactoryIoCommon().ioTransfer();
        this.tokenPool = new HmmTokenPool();
        this.rfs = RFS.of(transfer, this.tokenPool);
        this.bridge = new UploadAttachmentBridge();
    }

    @Override
    public Future<JsonObject> initSession(final JsonObject request, final XHeader header) {
        return Ux.waitVirtual(() -> {
            final JsonObject normalized = request == null ? new JsonObject() : request.copy();
            final String fileName = normalized.getString("fileName", normalized.getString(KName.NAME, VString.EMPTY));
            final Long totalSize = normalized.getLong("totalSize", 0L);
            final Long chunkSize = normalized.getLong("chunkSize", 2L * 1024 * 1024);
            final Long chunkCount = 0 == totalSize ? 0L : (long) Math.ceil((double) totalSize / chunkSize);
            final String identifier = normalized.getString(META_IDENTIFIER, VString.EMPTY);
            final String category = normalized.getString(META_CATEGORY, VString.EMPTY);
            final String directory = normalized.getString(META_DIRECTORY, "/upload/session");
            final String mime = normalized.getString(META_MIME, "application/octet-stream");
            final String checksum = normalized.getString(META_CHECKSUM);

            final Path sessionDir = this.sessionDirectory(header, identifier, fileName);
            try {
                Files.createDirectories(sessionDir);
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
            final Path finalPath = sessionDir.resolve(fileName);

            final TransferRequest transferRequest = new TransferRequest();
            transferRequest.setId(UUID.randomUUID());
            transferRequest.setNodeId(UUID.randomUUID());
            transferRequest.setType(TransferType.UPLOAD);
            transferRequest.setIsMultipart(Boolean.TRUE);
            transferRequest.setIsDirectory(Boolean.FALSE);
            transferRequest.setIsResume(Boolean.TRUE);
            transferRequest.setPathTarget(sessionDir.toString());
            transferRequest.setFileName(fileName);
            transferRequest.setTotalSize(totalSize);
            transferRequest.setChunkSize(chunkSize);
            transferRequest.setChunkCount(chunkCount);
            transferRequest.setUserId(this.uuid(header.session()));
            transferRequest.app(header.getAppId());
            transferRequest.tenant(header.getTenantId());

            final io.r2mo.typed.json.JObject parameters = io.r2mo.typed.json.JBase.parse("{}");
            parameters.put(HTransferParam.TOKEN.SERVICE_CONSUMER, "zero-exmodule-ambient");
            parameters.put(HTransferParam.TOKEN.SERVICE_PROVIDER, "r2mo-io-local");
            parameters.put(HTransferParam.REQUEST.MIME, mime);
            parameters.put(HTransferParam.REQUEST.EXTENSION, this.extensionOf(fileName));
            parameters.put(META_FINAL_PATH, finalPath.toString());
            parameters.put(META_IDENTIFIER, identifier);
            parameters.put(META_CATEGORY, category);
            parameters.put(META_DIRECTORY, directory);
            parameters.put(META_CHECKSUM, checksum);
            transferRequest.setParameters(parameters);

            final TransferResponse response = this.rfs.initRequest(transferRequest);
            final String token = response.getToken();
            final JsonObject context = new JsonObject();
            context.put(META_TOKEN, token);
            context.put(META_FILE_NAME, fileName);
            context.put(META_TOTAL_SIZE, totalSize);
            context.put(META_CHUNK_SIZE, chunkSize);
            context.put(META_CHUNK_COUNT, chunkCount);
            context.put(META_IDENTIFIER, identifier);
            context.put(META_CATEGORY, category);
            context.put(META_DIRECTORY, directory);
            context.put(META_MIME, mime);
            context.put(META_CHECKSUM, checksum);
            context.put(META_FINAL_PATH, finalPath.toString());
            context.put(META_APP_ID, header.getAppId());
            context.put(META_SIGMA, header.getSigma());
            context.put(META_TENANT_ID, header.getTenantId());
            context.put(META_UPDATED_BY, header.session());
            context.put(META_CREATED_AT, Instant.now().toString());
            this.persistContext(token, context);
            return io.vertx.core.Future.await(this.cache.put(token, context, CACHE_TTL_SECONDS));
        }).compose(saved -> this.sessionStatus(saved.getString(META_TOKEN), header));
    }

    @Override
    public Future<JsonObject> sessionStatus(final String token, final XHeader header) {
        return this.context(token).compose(context -> {
            final JsonObject result = new JsonObject();
            final List<StoreChunk> uploaded = this.rfs.getUploadedChunks(token);
            final List<StoreChunk> waiting = this.rfs.getWaitingChunks(token);
            result.put("token", token);
            result.put("fileName", context.getString(META_FILE_NAME));
            result.put("chunkSize", context.getLong(META_CHUNK_SIZE, 0L));
            result.put("chunkCount", context.getLong(META_CHUNK_COUNT, 0L));
            result.put("totalSize", context.getLong(META_TOTAL_SIZE, 0L));
            result.put("uploadedChunks", indexes(uploaded));
            result.put("waitingChunks", indexes(waiting));
            result.put("progress", this.rfs.getUploadProgress(token));
            result.put("status", this.rfs.isComplete(token) ? "UPLOADED" : "UPLOADING");
            return Ux.future(result);
        });
    }

    @Override
    public Future<JsonObject> uploadChunk(final String token, final Integer index, final Buffer buffer, final XHeader header) {
        return this.context(token).compose(context -> Ux.waitVirtual(() -> {
            final TransferResult result = this.rfs.ioUploadChunk(this.chunkRequest(token), new ByteArrayInputStream(buffer.getBytes()), index);
            final JsonObject response = new JsonObject();
            response.put("token", token);
            response.put("index", index);
            response.put("success", TransferResult.SUCCESS == result);
            response.put("progress", this.rfs.getUploadProgress(token));
            response.put("uploadedChunks", indexes(this.rfs.getUploadedChunks(token)));
            response.put("waitingChunks", indexes(this.rfs.getWaitingChunks(token)));
            return response;
        }));
    }

    @Override
    public Future<JsonObject> completeSession(final String token, final XHeader header) {
        return this.context(token).compose(context -> Ux.waitVirtual(() -> {
            final TransferResult result = this.rfs.completeUpload(token);
            if (TransferResult.SUCCESS != result) {
                return this.error("上传会话合并失败", token);
            }
            final JsonObject attachment = io.vertx.core.Future.await(this.bridge.createAttachment(context));
            io.vertx.core.Future.await(this.cache.remove(token));
            return this.completeResponse(token, attachment);
        }));
    }

    @Override
    public Future<JsonObject> cancelSession(final String token, final XHeader header) {
        return this.context(token).compose(context -> Ux.waitVirtual(() -> {
            this.rfs.cancelUpload(token);
            final String finalPath = context.getString(META_FINAL_PATH);
            if (Ut.isNotNil(finalPath) && Files.exists(Paths.get(finalPath))) {
                try {
                    Files.deleteIfExists(Paths.get(finalPath));
                } catch (final Exception ignore) {
                    // Ignore cleanup failure here
                }
            }
            io.vertx.core.Future.await(this.cache.remove(token));
            return new JsonObject()
                .put("token", token)
                .put("status", "CANCELLED")
                .put("success", Boolean.TRUE);
        }));
    }

    private Future<JsonObject> context(final String token) {
        return this.cache.find(token).compose(found -> {
            if (Objects.nonNull(found)) {
                return Ux.future(found);
            }
            final JsonObject recovered = this.recoverContext(token);
            if (Objects.nonNull(recovered)) {
                return this.cache.put(token, recovered, CACHE_TTL_SECONDS).compose(ignored -> Ux.future(recovered));
            }
            return Future.failedFuture("Upload session not found: " + token);
        });
    }

    private TransferRequest chunkRequest(final String token) {
        final TransferRequest request = new TransferRequest();
        request.setToken(token);
        request.setIsMultipart(Boolean.TRUE);
        return request;
    }

    private void persistContext(final String token, final JsonObject context) {
        final TransferToken transferToken = this.tokenPool.findBy(token);
        if (Objects.isNull(transferToken)) {
            return;
        }
        writeContext(transferToken, context);
        this.tokenPool.runSave(transferToken, this.expiredAt(transferToken));
    }

    private JsonObject recoverContext(final String token) {
        final TransferToken transferToken = this.tokenPool.findBy(token);
        if (Objects.isNull(transferToken)) {
            return null;
        }
        return readContext(transferToken);
    }

    private long expiredAt(final TransferToken transferToken) {
        if (Objects.nonNull(transferToken) && Objects.nonNull(transferToken.getExpiredAt())) {
            return transferToken.getExpiredAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return System.currentTimeMillis() + (CACHE_TTL_SECONDS * 1000L);
    }

    static void writeContext(final TransferToken transferToken, final JsonObject context) {
        if (Objects.isNull(transferToken) || Objects.isNull(context)) {
            return;
        }
        JObject configuration = transferToken.getConfiguration();
        if (Objects.isNull(configuration)) {
            configuration = JBase.parse("{}");
            transferToken.setConfiguration(configuration);
        }
        configuration.put(META_CONTEXT, JBase.parse(context.encode()));
    }

    static JsonObject readContext(final TransferToken transferToken) {
        if (Objects.isNull(transferToken) || Objects.isNull(transferToken.getConfiguration())) {
            return null;
        }
        final Object stored = transferToken.getConfiguration().get(META_CONTEXT);
        if (stored instanceof final JObject contextJ) {
            return new JsonObject(contextJ.encode());
        }
        if (stored instanceof final String contextText && Ut.isNotNil(contextText)) {
            return new JsonObject(contextText);
        }
        return null;
    }

    private JsonObject completeResponse(final String token, final JsonObject attachment) {
        return new JsonObject()
            .put("token", token)
            .put("status", "DONE")
            .put("key", attachment.getString(KName.KEY))
            .put("fileKey", attachment.getString(KName.FILE_KEY))
            .put("attachmentId", attachment.getString(KName.KEY));
    }

    private JsonObject error(final String message, final String token) {
        return new JsonObject().put("token", token).put("success", Boolean.FALSE).put("message", message);
    }

    private Path sessionDirectory(final XHeader header, final String identifier, final String fileName) {
        final AtConfig config = this.manager.config();
        final String root = Ut.isNil(config.getStorePath()) ? System.getProperty("java.io.tmpdir") : config.getStorePath();
        final String sigma = Ut.isNil(header.getSigma()) ? "default" : header.getSigma();
        final String appId = Ut.isNil(header.getAppId()) ? "default" : header.getAppId();
        final String safeIdentifier = Ut.isNil(identifier) ? "anonymous" : identifier;
        final String safeFileName = Ut.isNil(fileName) ? UUID.randomUUID().toString() : fileName;
        return Paths.get(root, "upload-session", sigma, appId, safeIdentifier, UUID.nameUUIDFromBytes((safeFileName + System.nanoTime()).getBytes()).toString());
    }

    private String extensionOf(final String fileName) {
        if (Ut.isNil(fileName) || !fileName.contains(".")) {
            return VString.EMPTY;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private UUID uuid(final String input) {
        if (Ut.isUUID(input)) {
            return UUID.fromString(input);
        }
        return null;
    }

    private static JsonArray indexes(final List<StoreChunk> chunks) {
        return new JsonArray(chunks.stream().map(StoreChunk::getIndex).sorted().collect(Collectors.toList()));
    }

    private static class UploadAttachmentBridge {
        Future<JsonObject> createAttachment(final JsonObject context) {
            final String fileName = context.getString(META_FILE_NAME);
            final String finalPath = context.getString(META_FINAL_PATH);
            final String mime = context.getString(META_MIME, "application/octet-stream");
            final String identifier = context.getString(META_IDENTIFIER);
            final String category = context.getString(META_CATEGORY);
            final String directory = context.getString(META_DIRECTORY);
            final JsonArray documents = new JsonArray();
            final JsonObject document = new JsonObject();
            final String fileKey = Ut.randomString(64);
            final String key = UUID.randomUUID().toString();
            document.put(KName.KEY, key);
            document.put(KName.STATUS, "DONE");
            document.put(KName.TYPE, mime);
            document.put(KName.MIME, mime);
            document.put(KName.NAME, fileName);
            document.put(KName.FILE_KEY, fileKey);
            document.put(KName.Attachment.FILE_NAME, stripExtension(fileName));
            document.put(KName.EXTENSION, extension(fileName));
            document.put(KName.SIZE, Long.valueOf(context.getLong(META_TOTAL_SIZE, 0L)).intValue());
            document.put(KName.Attachment.FILE_URL, MessageFormat.format(AtConstant.DOWNLOAD_URI, key));
            document.put(KName.Attachment.FILE_PATH, finalPath);
            document.put(KName.MODEL_ID, identifier);
            document.put(KName.MODEL_CATEGORY, category);
            document.put(KName.LANGUAGE, "cn");
            document.put(KName.METADATA, new JsonObject().put("uploadSession", Boolean.TRUE).encode());
            document.put(KName.Attachment.STORE_WAY, "FILE");
            document.put(KName.DIRECTORY, directory);
            document.put(KName.SIGMA, context.getString(META_SIGMA));
            document.put(KName.APP_ID, context.getString(META_APP_ID));
            document.put(KName.TENANT_ID, context.getString(META_TENANT_ID));
            document.put(KName.UPDATED_BY, context.getString(META_UPDATED_BY));
            document.put(KName.ACTIVE, Boolean.TRUE);
            documents.add(document);
            return new io.zerows.extension.module.ambient.spi.ExAttachmentNorm().uploadCompletedAsync(documents)
                .compose(saved -> Ux.future(saved.getJsonObject(0)));
        }

        private static String stripExtension(final String fileName) {
            if (Ut.isNil(fileName) || !fileName.contains(".")) {
                return fileName;
            }
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }

        private static String extension(final String fileName) {
            if (Ut.isNil(fileName) || !fileName.contains(".")) {
                return VString.EMPTY;
            }
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
    }
}
