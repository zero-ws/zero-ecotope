package io.zerows.extension.module.tpl.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.tpl.domain.tables.daos.MyNotifyDao;
import io.zerows.extension.module.tpl.domain.tables.pojos.MyNotify;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.extension.skeleton.spi.ExUser;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * @author lang : 2024-04-02
 */
public class NotifyService implements NotifyStub {

    @Override
    public Future<MyNotify> fetchNotify(final OwnerType ownerType, final String owner) {
        return this.fetchNotifyInternal(ownerType, owner).compose(notify -> {
            if (Objects.isNull(notify)) {
                return this.fetchNotifyInternal(owner);
            }
            return Ux.future(notify);
        });
    }

    private Future<MyNotify> fetchNotifyInternal(final String userId) {
        return HPI.of(ExUser.class).waitAsync(
                stub -> stub.userRole(userId),
                JsonArray::new
            ).compose(roleIds -> {
                final JsonObject condition = Ux.whereAnd();
                condition.put(KName.OWNER_TYPE, OwnerType.ROLE.name());
                condition.put(KName.OWNER_ID + ",i", roleIds);
                return DB.on(MyNotifyDao.class).<MyNotify>fetchAsync(condition);
            })
            .compose(roles -> {
                if (Objects.isNull(roles) || roles.isEmpty()) {
                    return Ux.future(null);
                } else {
                    // TODO: 暂时只提取第一个角色的提醒设置，后续更改
                    return Ux.future(roles.get(0));
                }
            });
    }

    public Future<MyNotify> fetchNotifyInternal(final OwnerType ownerType, final String owner) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.OWNER_TYPE, ownerType.name());
        condition.put(KName.OWNER_ID, owner);
        return DB.on(MyNotifyDao.class).fetchOneAsync(condition);
    }

    @Override
    public Future<MyNotify> saveNotify(final OwnerType ownerType, final String owner, final JsonObject data) {
        return this.fetchNotifyInternal(ownerType, owner).compose(notify -> {
            final MyNotify processed;
            if (Objects.isNull(notify)) {
                data.put(KName.OWNER_TYPE, ownerType.name());
                data.put(KName.OWNER_ID, owner);
                processed = Ux.fromJson(data, MyNotify.class);
                return DB.on(MyNotifyDao.class).insertAsync(processed);
            } else {
                processed = Ux.updateT(notify, data);
                return DB.on(MyNotifyDao.class).updateAsync(processed);
            }
        });
    }
}
