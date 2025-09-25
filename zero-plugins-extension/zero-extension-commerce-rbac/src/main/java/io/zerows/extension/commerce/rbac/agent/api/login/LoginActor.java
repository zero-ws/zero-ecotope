package io.zerows.extension.commerce.rbac.agent.api.login;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.unity.Ux;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.agent.service.login.AuthStub;
import io.zerows.extension.commerce.rbac.agent.service.login.pre.ImageStub;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.eon.Addr;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.module.domain.atom.commune.XHeader;
import io.zerows.module.domain.atom.typed.UObject;
import jakarta.inject.Inject;

import java.util.Objects;

/*
 * Auth Actor
 */
@Queue
public class LoginActor {

    private static final ScConfig CONFIG = ScPin.getConfig();

    @Inject
    private transient AuthStub stub;

    @Inject
    private transient ImageStub imageStub;

    @Address(Addr.Auth.LOGIN)
    public Future<JsonObject> login(final JsonObject user, final XHeader header) {
        final JsonObject params = user.copy();
        final String imageCode = Ut.valueString(params, AuthKey.CAPTCHA_IMAGE);
        return this.imageStub.verify(header.session(), imageCode)
            .compose(verified -> this.stub.login(params));
    }

    @Address(Addr.Auth.AUTHORIZE)
    public Future<JsonObject> authorize(final JsonObject data) {
        return this.stub.authorize(UObject.create(data).denull()
            .remove(AuthKey.RESPONSE_TYPE)
            .convert(AuthKey.CLIENT_ID, AuthKey.F_CLIENT_ID)
            .convert(AuthKey.CLIENT_SECRET, AuthKey.F_CLIENT_SECRET)
            .to());
    }

    @Address(Addr.Auth.TOKEN)
    public Future<JsonObject> token(final JsonObject data, final Session session) {
        return this.stub.token(data.copy(), session);
    }


    @Address(Addr.Auth.CAPTCHA_IMAGE_VERIFY)
    public Future<Boolean> imageVerity(final JsonObject request, final XHeader header) {
        Fx.out(Objects.isNull(header.session()), _501NotSupportException.class, this.getClass());
        final String imageCode = Ut.valueString(request, AuthKey.CAPTCHA_IMAGE);
        return this.imageStub.verify(header.session(), imageCode).compose(verified -> {
            final Boolean support = CONFIG.getSupportCaptcha();
            if (Objects.isNull(support) || !support) {
                // 输入验证码为空
                return Ut.Bnd.failOut(_501NotSupportException.class, this.getClass());
            }
            return Ux.futureT();
        });
    }

    /*
     * Default: 180 x 40
     */
    @Address(Addr.Auth.CAPTCHA_IMAGE)
    public Future<Buffer> generateImage(final XHeader header) {
        Fx.out(Objects.isNull(header.session()), _501NotSupportException.class, this.getClass());
        return this.imageStub.generate(header.session());
    }
}
