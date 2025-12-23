package io.zerows.extension.module.rbac.api;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.UObject;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.module.rbac.boot.MDRBACManager;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.extension.module.rbac.servicespec.AuthStub;
import io.zerows.extension.module.rbac.servicespec.ImageStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;

/*
 * Auth Actor
 */
@Queue
public class LoginActor {

    private static final ScConfig CONFIG = MDRBACManager.of().config();

    @Inject
    private transient AuthStub stub;

    @Inject
    private transient ImageStub imageStub;

    @Address(Addr.Auth.LOGIN)
    public Future<JsonObject> login(final JsonObject user, final XHeader header) {
        final JsonObject params = user.copy();
        final String imageCode = Ut.valueString(params, ScAuthKey.CAPTCHA_IMAGE);
        return this.imageStub.verify(header.session(), imageCode)
            .compose(verified -> this.stub.login(params));
    }

    @Address(Addr.Auth.AUTHORIZE)
    public Future<JsonObject> authorize(final JsonObject data) {
        return this.stub.authorize(UObject.create(data).denull()
            .remove(ScAuthKey.RESPONSE_TYPE)
            .convert(ScAuthKey.CLIENT_ID, ScAuthKey.F_CLIENT_ID)
            .convert(ScAuthKey.CLIENT_SECRET, ScAuthKey.F_CLIENT_SECRET)
            .to());
    }

    @Address(Addr.Auth.TOKEN)
    public Future<JsonObject> token(final JsonObject data, final Session session) {
        return this.stub.token(data.copy(), session);
    }


    @Address(Addr.Auth.CAPTCHA_IMAGE_VERIFY)
    public Future<Boolean> imageVerity(final JsonObject request, final XHeader header) {
        Fn.jvmKo(Objects.isNull(header.session()), _500ServerInternalException.class, "[ R2MO ] 会话不存在！");
        final String imageCode = Ut.valueString(request, ScAuthKey.CAPTCHA_IMAGE);
        return this.imageStub.verify(header.session(), imageCode).compose(verified -> {
            final Boolean support = CONFIG.getSupportCaptcha();
            if (Objects.isNull(support) || !support) {
                // 输入验证码为空
                return FnVertx.failOut(_501NotSupportException.class, "[ R2MO ] 验证码功能未开启！");
            }
            return Ux.futureT();
        });
    }

    /*
     * Default: 180 x 40
     */
    @Address(Addr.Auth.CAPTCHA_IMAGE)
    public Future<Buffer> generateImage(final XHeader header) {
        Fn.jvmKo(Objects.isNull(header.session()), _501NotSupportException.class, "[ R2MO ] 会话不存在，当前 API 不可用！");
        return this.imageStub.generate(header.session());
    }
}
