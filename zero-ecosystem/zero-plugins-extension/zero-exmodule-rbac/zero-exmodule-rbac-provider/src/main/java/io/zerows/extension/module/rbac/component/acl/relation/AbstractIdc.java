package io.zerows.extension.module.rbac.component.acl.relation;

import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.exception._60045Exception400SigmaMissing;
import io.zerows.extension.skeleton.spi.ScModeling;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.util.Objects;

public abstract class AbstractIdc implements IdcStub {
    protected transient final String sigma;

    public AbstractIdc(final String sigma) {
        this.sigma = sigma;
    }

    protected Future<JsonArray> model(final JsonArray userJson) {
        return HPI.of(ScModeling.class).waitAsync(
            stub -> stub.keyAsync(this.sigma, userJson).compose(keyMap -> {
                /* Reference 修改，此处修改 userJson 相关数据 */
                Ut.itJArray(userJson).forEach(user -> {
                    /* Fix issue of `modelKey` injection */
                    final String username = user.getString(KName.USERNAME);
                    final JsonObject data = keyMap.get(username);
                    if (Ut.isNotNil(data)) {
                        /*
                         * Replace
                         * - modelKey
                         * - modelId
                         * .etc here
                         */
                        user.mergeIn(data.copy(), true);
                    }
                });
                return Ux.future(userJson);
            }),
            () -> userJson
        );
    }

    /*
     * Model channel for modelId / modelKey
     */
    protected <T> Future<T> runPre(final T user) {
        if (Ut.isNil(this.sigma)) {
            return FnVertx.failOut(_60045Exception400SigmaMissing.class);
        }
        if (Objects.isNull(user)) {
            return FnVertx.failOut(_400BadRequestException.class, "[ R2MO ] 用户为空，无法继续操作！");
        }
        return Ux.future(user);
    }
}
