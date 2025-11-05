package io.zerows.extension.module.rbac.component.acl.rapier;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScOwner;
import io.zerows.extension.module.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class QuinnViews implements Quinn {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Future<T> fetchAsync(final String resourceId, final ScOwner owner) {
        final Set<String> roles = owner.roles();
        if (roles.isEmpty()) {
            return Ux.future((T) new ArrayList<>());
        }
        final JsonObject condition = Quinn.viewQr(resourceId, owner);
        // OWNER = ?, OWNER_TYPE = ? --- ownerType 固定
        condition.put(KName.OWNER + ",i", Ut.toJArray(roles));
        condition.put(KName.OWNER_TYPE, OwnerType.ROLE.name());
        return DB.on(SViewDao.class).fetchAsync(condition)
            .compose(list -> Ux.future((T) list));
    }
}
