package io.zerows.extension.module.ambient.api;

import io.r2mo.base.io.modeling.FileRange;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.ambient.servicespec.DocRStub;
import io.zerows.extension.module.ambient.servicespec.UploadStub;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Queue
@Slf4j
public class AttachActor {

    @Inject
    private transient DocRStub reader;
    @Inject
    private transient UploadStub uploadStub;
    @Inject
    private transient ExAttachment attachment;

    @Address(Addr.File.UPLOAD)
    public Future<JsonObject> upload(final JsonObject content, final XHeader header) {
        log.info("[ XMOD ] 上传参数：{}", content.encodePrettily());
        Ut.valueToJObject(content, KName.METADATA);
        content.put(KName.SIGMA, header.getSigma());
        content.put(KName.ACTIVE, Boolean.TRUE);
        return Ux.future(content);
    }

    @Address(Addr.File.UPLOAD_SESSION_INIT)
    public Future<JsonObject> initSession(final JsonObject content, final XHeader header) {
        return this.uploadStub.initSession(content, header);
    }

    @Address(Addr.File.UPLOAD_SESSION_STATUS)
    public Future<JsonObject> sessionStatus(final String token, final XHeader header) {
        return this.uploadStub.sessionStatus(token, header);
    }

    @Address(Addr.File.UPLOAD_SESSION_CHUNK)
    public Future<JsonObject> uploadChunk(final String token, final Integer index, final Buffer buffer, final XHeader header) {
        return this.uploadStub.uploadChunk(token, index, buffer, header);
    }

    @Address(Addr.File.UPLOAD_SESSION_COMPLETE)
    public Future<JsonObject> completeSession(final String token, final XHeader header) {
        return this.uploadStub.completeSession(token, header);
    }

    @Address(Addr.File.UPLOAD_SESSION_CANCEL)
    public Future<JsonObject> cancelSession(final String token, final XHeader header) {
        return this.uploadStub.cancelSession(token, header);
    }

    @Address(Addr.File.DOWNLOAD)
    public Future<Envelop> download(final Envelop request) {
        final JsonObject filters = request.data(JsonObject.class);
        log.info("[ XMOD ] 下载条件：{}", filters.encodePrettily());
        final String key = filters.getString(KName.KEY);
        final String rangeHeader = filters.getString("Range");
        final FileRange range = FileRange.of(rangeHeader);
        return this.ensureFileSize(filters)
            .compose(enriched -> {
                final Future<Buffer> downloaded = Objects.nonNull(range) ?
                    this.reader.downloadDoc(key, range) : this.reader.downloadDoc(key);
                return downloaded.compose(buffer -> Ux.future(this.replyDownload(request, enriched, buffer)));
            });
    }

    static Envelop replyDownload(final Envelop request, final JsonObject filters, final Buffer buffer) {
        final Envelop response = Envelop.success(buffer);
        response.from(request);
        final Object fileSize = filters.getValue(KName.SIZE);
        if (Objects.nonNull(fileSize)) {
            final Map<String, Object> context = new HashMap<>();
            if (Objects.nonNull(request.context())) {
                context.putAll(request.context().data());
            }
            context.put("X_FILE_SIZE", fileSize);
            response.content(context);
        }
        return response;
    }

    private Future<JsonObject> ensureFileSize(final JsonObject filters) {
        if (Objects.nonNull(filters.getValue(KName.SIZE))) {
            return Ux.future(filters);
        }
        if (Objects.isNull(this.attachment)) {
            return Ux.future(filters);
        }
        final String key = filters.getString(KName.KEY);
        if (Ut.isNil(key)) {
            return Ux.future(filters);
        }
        final JsonObject condition = new JsonObject();
        if (Ut.isUUID(key)) {
            condition.put(KName.KEY, key);
        } else {
            condition.put(KName.FILE_KEY, key);
        }
        return this.attachment.fetchAsync(condition).compose(found -> {
            if (Ut.isNotNil(found)) {
                final JsonObject first = found.getJsonObject(0);
                if (Objects.nonNull(first) && Objects.nonNull(first.getValue(KName.SIZE))) {
                    filters.put(KName.SIZE, first.getValue(KName.SIZE));
                }
            }
            return Ux.future(filters);
        });
    }
}
