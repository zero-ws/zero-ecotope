package io.zerows.extension.runtime.ambient.agent.service.linkage;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.ambient.domain.tables.daos.RTagObjectDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTagDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.RTagObject;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XTag;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2023-09-28
 */
public class TagService implements TagStub {

    @Override
    public Future<JsonObject> saveAsync(final JsonObject body) {
        // 按 name 查找
        final String name = Ut.valueString(body, KName.NAME);
        final JsonObject qr = Ux.whereAnd();
        qr.put(KName.NAME, name);
        qr.put(KName.SIGMA, Ut.valueString(body, KName.SIGMA));
        return Ux.Jooq.on(XTagDao.class).<XTag>fetchOneAsync(qr)
            .compose(entity -> {
                if (Objects.isNull(entity)) {
                    final XTag inserted = Ux.fromJson(body, XTag.class);
                    return Ux.Jooq.on(XTagDao.class).insertAsync(inserted);
                } else {
                    final XTag updated = Ux.updateT(entity, body);
                    return Ux.Jooq.on(XTagDao.class).updateAsync(updated);
                }
            })
            .compose(synced -> {
                final String tagId = synced.getKey();
                final String entityType = Ut.valueString(body, "entityType");
                final String entityId = Ut.valueString(body, "entityId");
                return this.saveObjects(entityType, entityId, tagId)
                    .compose(nil -> Ux.futureJ(synced));
            });
    }

    private Future<Boolean> saveObjects(final String entityType,
                                        final String entityId,
                                        final String tagId) {
        final JsonObject qr = Ux.whereAnd();
        qr.put("entityType", entityType);
        qr.put("entityId", entityId);
        qr.put("tagId", tagId);
        return Ux.Jooq.on(RTagObjectDao.class).<RTagObject>fetchOneAsync(qr).compose(entity -> {
            if (Objects.isNull(entity)) {
                final RTagObject rTagObject = new RTagObject();
                rTagObject.setEntityType(entityType);
                rTagObject.setEntityId(entityId);
                rTagObject.setTagId(tagId);
                return Ux.Jooq.on(RTagObjectDao.class).insertAsync(rTagObject);
            } else {
                return Ux.future();
            }
        }).compose(nil -> Ux.future(Boolean.TRUE));
    }

    @Override
    public Future<Boolean> deleteAsync(final String key) {
        // 删除当前 Tag
        // 删除和当前 Tag 相关的所有 TagObject
        final JsonObject qr = Ux.whereAnd();
        qr.put("tagId", key);
        return Ux.Jooq.on(RTagObjectDao.class).deleteByAsync(qr)
            .compose(nil -> Ux.Jooq.on(XTagDao.class).deleteByIdAsync(key));
    }

    @Override
    public Future<List<RTagObject>> fetchAsync(final String modelId, final String modelKey) {
        final JsonObject qr = Ux.whereAnd();
        qr.put("entityType", modelId);
        qr.put("entityId", modelKey);
        return Ux.Jooq.on(RTagObjectDao.class).fetchAsync(qr);
    }
}
