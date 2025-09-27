package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.zerows.core.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExSetting;
import io.zerows.extension.runtime.skeleton.secure.Twine;

/**
 * 读取用户个人设置专用，对接 MY_ 系列表，走通道结构
 *
 * @author lang : 2024-04-09
 */
public class TwineSetting implements Twine<SUser> {
    @Override
    public Future<JsonObject> identAsync(final JsonObject userJ) {
        final String userId = Ut.valueString(userJ, KName.KEY);
        final String sigma = Ut.valueString(userJ, KName.SIGMA);
        return Ux.channel(ExSetting.class, JsonObject::new, stub -> stub.settingAsync(userId, sigma))
            .compose(settings -> {
                userJ.put(KName.SETTING, settings);
                return Ux.future(userJ);
            });
    }

    @Override
    public Future<JsonObject> identAsync(final SUser key) {
        return Ut.Bnd.failOut(_501NotSupportException.class, this.getClass());
    }

    @Override
    public Future<JsonObject> identAsync(final SUser userJ, final JsonObject updatedJ) {
        return Ut.Bnd.failOut(_501NotSupportException.class, this.getClass());
    }
}
