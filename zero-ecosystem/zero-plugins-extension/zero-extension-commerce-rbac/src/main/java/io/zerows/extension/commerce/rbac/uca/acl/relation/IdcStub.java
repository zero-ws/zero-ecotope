package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.corpus.Ux;
import io.zerows.platform.exception._60050Exception501NotSupport;

/*
 * Uniform `SUser` created
 * 1. SUser
 * 2. OUser
 * 3. SRole
 */
public interface IdcStub {

    Cc<String, IdcStub> CC_STUB = Cc.open();

    static IdcStub create(final String sigma) {
        /*
         * Each sigma has one reference of `IdcStub`
         */
        return CC_STUB.pick(() -> new IdcService(sigma), sigma);
    }

    /*
     * Save user information for
     * 1) Add
     * 2) Update
     */
    default Future<JsonArray> saveAsync(final JsonArray user, final String by) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<JsonObject> saveAsync(final JsonObject user, final String by) {
        final JsonArray users = new JsonArray();
        users.add(user);
        return this.saveAsync(users, by).compose(array -> Ux.future(array.getJsonObject(VValue.IDX)));
    }
}
