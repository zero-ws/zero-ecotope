package io.zerows.extension.commerce.rbac.agent.api.login;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.commerce.rbac.eon.Addr;

/**
 * @author lang : 2024-07-07
 */
@Queue
public class WechatActor {

    @Address(Addr.Auth.Extension.WECHAT_QR)
    public Future<JsonObject> generateWechat(final JsonObject params) {
        return null;
    }

    @Address(Addr.Auth.Extension.WECHAT_LOGIN)
    public Future<JsonObject> loginWechat(final JsonObject params) {
        return null;
    }
}
