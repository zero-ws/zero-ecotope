package io.zerows.extension.commerce.rbac.agent.service.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SView;
import io.zerows.extension.runtime.skeleton.eon.em.OwnerType;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class PersonalService implements PersonalStub {
    @Override
    public Future<List<SView>> byUser(final String resourceId, final String ownerId, final String position) {
        final JsonObject criteria = Ux.whereAnd();
        criteria.put("ownerType", OwnerType.USER.name());
        criteria.put("owner", ownerId);
        criteria.put(KName.RESOURCE_ID, resourceId);
        criteria.put(KName.POSITION, Objects.isNull(position) ? VValue.DFT.V_POSITION : position);
        return DB.on(SViewDao.class).fetchAsync(criteria);
    }

    @Override
    public Future<SView> create(final JsonObject data) {
        Ut.valueToString(data, VName.KEY_CRITERIA, VName.KEY_PROJECTION, "rows");
        final SView view = Ut.deserialize(data, SView.class);
        if (data.containsKey(KName.USER)) {
            view.setCreatedBy(data.getString(KName.USER));
            view.setUpdatedBy(data.getString(KName.USER));
        }
        view.setCreatedAt(LocalDateTime.now());
        view.setUpdatedAt(LocalDateTime.now());
        view.setKey(UUID.randomUUID().toString());
        view.setActive(Boolean.TRUE);
        return DB.on(SViewDao.class).insertAsync(view);
    }

    @Override
    public Future<Boolean> delete(final Set<String> keys) {
        if (1 == keys.size()) {
            final String key = keys.iterator().next();
            return DB.on(SViewDao.class).deleteByIdAsync(key);
        } else {
            final JsonObject criteria = Ux.whereKeys(keys);
            return DB.on(SViewDao.class).deleteByAsync(criteria);
        }
    }

    @Override
    public Future<SView> byId(final String key) {
        return DB.on(SViewDao.class).fetchByIdAsync(key);
    }

    @Override
    public Future<SView> update(final String key, final JsonObject data) {
        return this.byId(key).compose(view -> {
            if (Objects.isNull(view)) {
                return Ux.future();
            } else {
                final JsonObject serialized = Ut.serializeJson(view);
                Ut.valueToString(data, VName.KEY_CRITERIA, VName.KEY_PROJECTION, "rows");
                if (data.containsKey(KName.USER)) {
                    view.setUpdatedBy(data.getString(KName.USER));
                    view.setUpdatedAt(LocalDateTime.now());
                }
                serialized.mergeIn(data, true);
                return DB.on(SViewDao.class).updateAsync(serialized);
            }
        });
    }
}
