package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;

/**
 * @author lang : 2024-07-07
 */
@Queue
public class LoginWechatActor {

    @Address(Addr.Auth.Extension.WECHAT_QR)
    public Future<JsonObject> generateWechat(final JsonObject params) {
        return null;
    }

    @Address(Addr.Auth.Extension.WECHAT_LOGIN)
    public Future<JsonObject> loginWechat(final JsonObject params) {
        return null;
    }
}
