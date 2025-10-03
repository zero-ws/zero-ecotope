package io.zerows.extension.commerce.rbac.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.component.aop.After;
import io.zerows.epoch.corpus.Ux;
import io.zerows.enums.typed.ChangeFlag;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.uca.acl.relation.IdcStub;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AfterUserSync implements After {
    @Override
    public Set<ChangeFlag> types() {
        return new HashSet<>() {
            {
                this.add(ChangeFlag.ADD);
                this.add(ChangeFlag.UPDATE);
            }
        };
    }

    @Override
    public Future<JsonArray> afterAsync(final JsonArray data, final JsonObject config) {
        final JsonArray users = new JsonArray();
        // JsonObject Extract from config;
        final JsonObject mapping = Ut.valueJObject(config, KName.MAPPING);
        final JsonObject initialize = Ut.valueJObject(config, KName.INITIALIZE);
        Ut.itJArray(data).forEach(employee -> {
            final JsonObject inputJ = Ut.valueJObject(employee, KName.__.INPUT);
            if (inputJ.containsKey(KName.USERNAME)) {
                // Put initialize into created users
                final JsonObject userJ = initialize.copy();
                // 8 Normalized Fields
                Ke.umCreated(userJ, employee);
                // Mapping processing
                Ut.<String>itJObject(mapping, (to, from) -> userJ.put(to, employee.getValue(from)));
                // Input Extracting
                userJ.put(KName.USERNAME, inputJ.getValue(KName.USERNAME));
                final String roles = inputJ.getString("roles", null);
                if (Ut.isNotNil(roles)) {
                    userJ.put("roles", roles);
                }
                userJ.put(KName.MODEL_KEY, employee.getValue(KName.KEY));
                users.add(userJ);
            }
        });
        if (Ut.isNil(users)) {
            return Ux.future(data);
        } else {
            final String sigma = Ut.valueString(data, KName.SIGMA);
            final String by = Ut.valueString(data, KName.UPDATED_BY);
            final IdcStub idcStub = IdcStub.create(sigma);
            return idcStub.saveAsync(users, by).compose(created -> Ux.future(data));
        }
    }
}
