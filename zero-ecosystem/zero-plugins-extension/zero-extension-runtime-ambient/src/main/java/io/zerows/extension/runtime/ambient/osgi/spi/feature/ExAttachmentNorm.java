package io.zerows.extension.runtime.ambient.osgi.spi.feature;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XAttachmentDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XAttachment;
import io.zerows.extension.runtime.ambient.util.At;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.extension.skeleton.spi.ExIo;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExAttachmentNorm implements ExAttachment {
    private static final LogOf LOGGER = LogOf.get(ExAttachmentNorm.class);


    @Override
    public Future<JsonArray> saveAsync(final JsonObject condition, final JsonArray data) {
        if (Ut.isNil(data)) {
            return Ux.futureA();
        } else {
            final JsonObject params = this.uploadParams(data);
            return this.saveAsync(condition, data, params);
        }
    }

    @Override
    public Future<JsonArray> saveAsync(final JsonObject condition, final JsonArray data, final JsonObject params) {

        final ADB jq = DB.on(XAttachmentDao.class);
        return jq.fetchJAsync(condition).compose(original -> {
            // 计算的三通道
            final ConcurrentMap<ChangeFlag, JsonArray> compared =
                Ux.compareJ(original, data, KName.KEY);
            // 删除通道
            final JsonArray deleted = compared.getOrDefault(ChangeFlag.DELETE, new JsonArray());
            // 1. 删除
            return jq.deleteJAsync(deleted).compose(nil -> At.fileRemove(deleted))
                // 2. 添加
                .compose(nil -> {
                    final JsonArray added = compared.getOrDefault(ChangeFlag.ADD, new JsonArray());
                    return this.uploadAsync(added, params);
                })
                // 3. 组合更新和添加
                .compose(added -> {
                    final JsonArray combine = compared.getOrDefault(ChangeFlag.UPDATE, new JsonArray());
                    if (Ut.isNotNil(added)) {
                        combine.addAll(added);
                    }
                    return Ux.future(combine);
                });
        });
    }

    @Override
    public Future<JsonArray> uploadAsync(final JsonArray data) {
        /*
         * 此处的参数设置主要目的是防止 AtFs 中的 NullPointer 问题
         */
        final JsonObject params = this.uploadParams(data);
        return this.uploadAsync(data, params);
    }

    private JsonObject uploadParams(final JsonArray data) {
        final String sigma = Ut.valueString(data, KName.SIGMA);
        final String directory = Ut.valueString(data, KName.DIRECTORY);
        final String updatedBy = Ut.valueString(data, KName.UPDATED_BY);
        final JsonObject params = new JsonObject();
        params.put(KName.SIGMA, sigma);
        params.put(KName.DIRECTORY, directory);
        params.put(KName.UPDATED_BY, updatedBy);
        return params;
    }

    @Override
    public Future<JsonArray> uploadAsync(final JsonArray data, final JsonObject params) {
        if (Ut.isNil(data)) {
            return Ux.futureA();
        }

        return At.fileDir(data, params).compose(normalized -> {
            // Fix: com.fasterxml.jackson.databind.exc.MismatchedInputException:
            // Cannot deserialize get of type `java.lang.String` from Object get (token `JsonToken.START_OBJECT`)
            Ut.valueToString(normalized, KName.METADATA);
            final List<XAttachment> attachments = Ux.fromJson(normalized, XAttachment.class);
            return DB.on(XAttachmentDao.class).insertJAsync(attachments)

                // ExIo -> Call ExIo to impact actual file system ( Store )
                .compose(At::fileUpload)
                .compose(this::outAsync);
        });
    }

    @Override
    public Future<JsonArray> updateAsync(final JsonArray attachmentJ, final boolean active) {
        Ut.valueToString(attachmentJ, KName.METADATA);
        Ut.itJArray(attachmentJ).forEach(attachment -> {
            attachment.put(KName.UPDATED_AT, Instant.now());
            attachment.put(KName.ACTIVE, active);
        });
        final List<XAttachment> attachments = Ux.fromJson(attachmentJ, XAttachment.class);
        return DB.on(XAttachmentDao.class).updateAsyncJ(attachments)
            .compose(Fx.ofJArray(KName.METADATA));
    }

    @Override
    public Future<JsonArray> removeAsync(final JsonObject condition) {
        return DB.on(XAttachmentDao.class).fetchJAsync(condition)
            .compose(attachments -> this.removeAsyncInternal(condition, attachments));
    }

    @Override
    public Future<JsonArray> purgeAsync(final JsonArray attachment) {
        final JsonObject criteria = new JsonObject();
        criteria.put(KName.KEY + ",i", Ut.toJArray(Ut.valueSetString(attachment, KName.KEY)));
        return this.removeAsyncInternal(criteria, attachment);
    }


    @Override
    public Future<JsonArray> fetchAsync(final JsonObject condition) {
        LOG.File.info(LOGGER, "Fetch Operation, condition: {0}", condition);
        return DB.on(XAttachmentDao.class)
            .fetchJAsync(condition)
            .compose(this::outAsync);
    }

    @Override
    public Future<Buffer> downloadAsync(final Set<String> keys) {
        final JsonObject condition = new JsonObject();
        condition.put(KName.KEY + ",i", Ut.toJArray(keys));
        return this.fetchAsync(condition)

            // ExIo -> Call ExIo to impact actual file system ( Store )
            .compose(At::fileDownload);
    }

    @Override
    public Future<Buffer> downloadAsync(final String key) {
        /*
         * 此处使用双条件，key 只会有两种格式
         * 1）主键，KEY = key
         * 2）文件唯一键，FILE_KEY = key
         * 所以使用双条件查询以保证附件下载的完备性，此处依赖 UUID 的判断条件
         */
        final JsonObject condition = new JsonObject();
        if (Ut.isUUID(key)) {
            condition.put(KName.KEY, key);
        } else {
            condition.put(KName.FILE_KEY, key);
        }
        LOG.File.info(LOGGER, "Fetch Operation, condition: {0}", condition);
        return DB.on(XAttachmentDao.class).fetchJOneAsync(condition)

            // ExIo -> Call ExIo to impact actual file system ( Store )
            .compose(At::fileDownload);
    }

    // ----------------- Private Method Interface ----------------------

    private Future<JsonArray> outAsync(final JsonArray files) {
        /*
         * Fetch `visit` information
         */
        final Set<String> keys = Ut.valueSetString(files, KName.DIRECTORY_ID);
        return Ux.channel(ExIo.class, () -> files, io -> io.dirBy(keys).compose(map -> {
            Ut.itJArray(files).forEach(file -> {
                final String directoryId = file.getString(KName.DIRECTORY_ID);
                if (Ut.isNotNil(directoryId)) {
                    final JsonObject directoryJ = map.getOrDefault(directoryId, new JsonObject());
                    final JsonObject visitJ = Ut.elementSubset(directoryJ,
                        KName.VISIT_ROLE,
                        KName.VISIT_GROUP,
                        KName.VISIT,
                        KName.VISIT_MODE
                    );
                    /*
                     * visitMode switching
                     *
                     * 1. If directory contains "w",
                     * 2. The attachment should append "x" for rename/trash
                     */
                    final JsonArray visitMode = Ut.valueJArray(visitJ, KName.VISIT_MODE);
                    if (visitMode.contains(KName.Attachment.W) &&
                        !visitMode.contains(KName.Attachment.X)) {
                        visitMode.add(KName.Attachment.X);
                        visitJ.put(KName.VISIT_MODE, visitMode);
                    }
                    file.mergeIn(visitJ, true);
                }
            });
            return Ux.future(files);
        })).compose(Fx.ofJArray(KName.METADATA)).compose(attachments -> {
            Ut.itJArray(attachments).forEach(file -> file.put(KName.DIRECTORY, Boolean.FALSE));
            return Ux.future(attachments);
        });
    }

    private Future<JsonArray> removeAsyncInternal(final JsonObject condition, final JsonArray attachments) {
        LOG.File.info(LOGGER, "Remove Operation, condition: {0}", condition);
        return DB.on(XAttachmentDao.class).deleteByAsync(condition)

            // ExIo -> Call ExIo to impact actual file system ( Store )
            .compose(removed -> At.fileRemove(attachments))
            .compose(this::outAsync);
    }
}
