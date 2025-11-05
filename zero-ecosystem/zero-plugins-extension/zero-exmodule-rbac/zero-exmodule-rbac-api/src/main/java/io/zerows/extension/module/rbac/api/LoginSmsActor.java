package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.module.rbac.servicespec.SmsStub;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-07-07
 */
@Queue
public class LoginSmsActor {

    @Inject
    private SmsStub smsStub;

    @Address(Addr.Auth.Extension.SMS_CAPTCHA)
    public Future<Boolean> generateSms(final JsonObject params, final XHeader header) {
        // 验证手机号是否存在
        return this.smsStub.send(header.session(), params);
    }

    @Address(Addr.Auth.Extension.SMS_LOGIN)
    public Future<JsonObject> loginSms(final JsonObject params, final Session session,
                                       final XHeader header) {
        return this.smsStub.login(header.session(), params, session);
    }
}
