package io.zerows.extension.module.rbac.serviceauth;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.exception._80200Exception401CodeWrong;
import io.zerows.extension.module.rbac.servicespec.AuthRegisterStub;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.extension.skeleton.spi.ExAccountProvision;
import io.zerows.plugins.security.email.EmailLoginRequest;
import io.zerows.plugins.security.email.EmailStub;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import io.zerows.plugins.security.sms.SmsLoginRequest;
import io.zerows.plugins.security.sms.SmsStub;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import jakarta.inject.Inject;

import java.util.Locale;
import java.util.Objects;

public class AuthRegisterService implements AuthRegisterStub {

    @Inject
    private EmailStub emailStub;
    @Inject
    private SmsStub smsStub;
    @Inject
    private AuthLoginStub loginStub;
    @Inject
    private UserAuthStub userAuthStub;

    @Override
    public Future<JsonObject> register(final JsonObject params) {
        final String type = params.getString("type", "").toUpperCase(Locale.ROOT);
        return switch (type) {
            case "EMAIL" -> this.registerEmail(params);
            case "SMS" -> this.registerSms(params);
            default -> FnVertx.failOut(_400BadRequestException.class,
                "[ ZERO ] Unsupported register type: " + type);
        };
    }

    private Future<JsonObject> registerEmail(final JsonObject params) {
        final EmailLoginRequest request = new EmailLoginRequest();
        request.setEmail(this.identifier(params, "email"));
        request.setCaptcha(params.getString("captcha"));
        return request.requestValidated()
            .compose(nil -> this.emailStub.verifyRegistration(request))
            .compose(verified -> this.afterVerified(params, request, TypeLogin.EMAIL, verified));
    }

    private Future<JsonObject> registerSms(final JsonObject params) {
        final SmsLoginRequest request = new SmsLoginRequest();
        request.setMobile(this.identifier(params, "mobile"));
        request.setCaptcha(params.getString("captcha"));
        return request.requestValidated()
            .compose(nil -> this.smsStub.verifyRegistration(request))
            .compose(verified -> this.afterVerified(params, request, TypeLogin.SMS, verified));
    }

    private Future<JsonObject> afterVerified(final JsonObject params, final LoginRequest request,
                                             final TypeLogin type, final Boolean verified) {
        if (!Boolean.TRUE.equals(verified)) {
            return FnVertx.failOut(_80200Exception401CodeWrong.class, request.getCredential());
        }
        return this.userAuthStub.whereBy(request.getId(), type)
            .compose(found -> this.afterFound(params, request, type, found));
    }

    private Future<JsonObject> afterFound(final JsonObject params, final LoginRequest request,
                                          final TypeLogin type, final Object found) {
        if (Objects.nonNull(found)) {
            return this.login(request)
                .map(response -> response
                    .put("existing", Boolean.TRUE)
                    .put("setupRequired", Boolean.FALSE)
                    .put("type", type.name())
                    .put("identifier", request.getId()));
        }
        if (!this.readyForProvision(params)) {
            return Ux.futureJ(new JsonObject()
                .put("existing", Boolean.FALSE)
                .put("setupRequired", Boolean.TRUE)
                .put("type", type.name())
                .put("identifier", request.getId())
                .put("username", params.getString("username")));
        }
        final JsonObject input = params.copy()
            .put("identifier", request.getId())
            .put("type", type.name());
        return HPI.of(ExAccountProvision.class).waitOr(
            provision -> provision.provision(input),
            () -> Ux.futureJ(new JsonObject())
        ).compose(nil -> this.login(request)
            .map(response -> response
                .put("existing", Boolean.FALSE)
                .put("setupRequired", Boolean.FALSE)
                .put("type", type.name())
                .put("identifier", request.getId())));
    }

    private Future<JsonObject> login(final LoginRequest request) {
        return this.loginStub.login(request)
            .compose(userAt -> new TokenDynamicResponse(userAt).response()
                .map(response -> this.appendUser(response, userAt)));
    }

    private JsonObject appendUser(final JsonObject response, final UserAt userAt) {
        if (Objects.nonNull(userAt)
            && Objects.nonNull(userAt.logged())
            && StrUtil.isNotBlank(userAt.logged().getUsername())) {
            response.put("username", userAt.logged().getUsername());
        }
        return response;
    }

    private boolean readyForProvision(final JsonObject params) {
        return StrUtil.isNotBlank(params.getString("username"))
            && StrUtil.isNotBlank(params.getString("password"));
    }

    private String identifier(final JsonObject params, final String field) {
        final String direct = params.getString(field);
        if (StrUtil.isNotBlank(direct)) {
            return direct;
        }
        return params.getString("identifier");
    }
}
