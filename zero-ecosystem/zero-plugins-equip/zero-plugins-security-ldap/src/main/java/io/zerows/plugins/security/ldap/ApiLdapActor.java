package io.zerows.plugins.security.ldap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class ApiLdapActor {
    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Address(ApiAddr.API_AUTH_LDAP_LOGIN)
    public Future<JsonObject> login(final LdapLoginRequest request, final Vertx vertx) {
        // username, password 非空校验，此处 username 是邮箱信息
        return request.requestValidated()
            // captcha / captchaId 可选非空校验
            .compose(this.captchaStub::validateRequired)
            // 验证码校验
            .compose(validated -> this.captchaStub.validate(validated.captchaId(), validated.captcha()))
            // LDAP 登录验证
            .compose(nil -> this.validateLdap(request, vertx))
            // 生成登录响应验证
            .compose(nil -> this.loginStub.login(request))
            .compose(userAt -> new TokenDynamicResponse(userAt).response());
    }

    private Future<Boolean> validateLdap(final LdapLoginRequest request, final Vertx vertx) {
        // 凭证信息
        final UsernamePasswordCredentials creds = request.credentials();
        // 调用 LDAP 验证（显示调用）
        return LdapManager.of(vertx).getProvider().authenticate(creds)
            // ✅ 认证成功！
            // LDAP 只负责告诉你“密码对了”，它不负责生成 Token。
            // 这里你需要自己生成 JWT。
            .map(Objects::nonNull);
    }
}
