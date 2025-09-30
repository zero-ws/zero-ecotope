package io.zerows.extension.commerce.rbac.uca.acl.rapier;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.atom.ScOwner;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.runtime.skeleton.eon.em.OwnerType;
import io.zerows.unity.Ux;

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
        return Ux.Jooq.on(SViewDao.class).fetchAsync(condition)
            .compose(list -> Ux.future((T) list));
    }
}
