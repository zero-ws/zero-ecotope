package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.exception.web._400BadRequestException;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.skeleton.exception._400SigmaMissingException;
import io.zerows.extension.runtime.skeleton.osgi.spi.environment.Modeling;
import io.zerows.unity.Ux;

import java.util.Objects;

public abstract class AbstractIdc implements IdcStub {
    protected transient final String sigma;

    public AbstractIdc(final String sigma) {
        this.sigma = sigma;
    }

    protected Future<JsonArray> model(final JsonArray userJson) {
        return Ux.channelA(Modeling.class, () -> Ux.future(userJson), stub -> stub.keyAsync(this.sigma, userJson).compose(keyMap -> {
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
        }));
    }

    /*
     * Model channel for modelId / modelKey
     */
    protected <T> Future<T> runPre(final T user) {
        if (Ut.isNil(this.sigma)) {
            return Ut.Bnd.failOut(_400SigmaMissingException.class, this.getClass());
        }
        if (Objects.isNull(user)) {
            return Ut.Bnd.failOut(_400BadRequestException.class, this.getClass());
        }
        return Ux.future(user);
    }
}
