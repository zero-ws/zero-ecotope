package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ExSetting;
import io.zerows.extension.skeleton.spi.ScTwine;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * 读取用户个人设置专用，对接 MY_ 系列表，走通道结构
 *
 * @author lang : 2024-04-09
 */
public class TwineSetting implements ScTwine<SUser> {
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
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    @Override
    public Future<JsonObject> identAsync(final SUser userJ, final JsonObject updatedJ) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
