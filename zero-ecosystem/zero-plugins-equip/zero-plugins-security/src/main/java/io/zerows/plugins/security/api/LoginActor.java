package io.zerows.plugins.security.api;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.exception._80216Exception403CaptchaProfile;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.plugins.security.metadata.YmSecurityDevelopment;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.BasicLoginRequest;
import io.zerows.plugins.security.service.BasicLoginResponse;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.support.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class LoginActor {

    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Address(Addr.API_AUTH_LOGIN)
    public Future<JsonObject> login(final BasicLoginRequest request, final XHeader headers) {
        // username, password 非空校验
        final boolean whenDev = this.whenDevelopment(request, headers);
        if (whenDev) {
            // 跳过验证码校验，直接登录处理
            return this.loginStub.login(request)
                .compose(userAt -> new BasicLoginResponse(userAt).response());
        } else {
            return request.requestValidated()
                // captcha / captchaId 可选非空校验
                .compose(this.captchaStub::validateRequired)
                // 验证码校验
                .compose(validated -> this.captchaStub.validate(validated.captchaId(), validated.captcha()))
                // 登录处理
                .compose(nil -> this.loginStub.login(request))
                .compose(userAt -> new BasicLoginResponse(userAt).response());
        }
    }

    /**
     * 新配置
     * <pre>
     *     security:
     *       development:
     *         header-name:
     *         header-value:
     * </pre>
     *
     * @param request 请求对象
     * @return 验证码校验结果
     */
    private boolean whenDevelopment(final BasicLoginRequest request, final XHeader headers) {
        // 安全配置校验（只校验 development 节点）
        final YmSecurity security = SecurityActor.configuration();
        final YmSecurityDevelopment development = security.getDevelopment();
        if (Objects.isNull(development) || Objects.isNull(headers)) {
            // 未配置 development，执行校验码验证
            return false;
        }
        final String headerName = development.getHeaderName();
        final String headerValue = development.getHeaderValue();
        if (StrUtil.isEmpty(headerName) || StrUtil.isEmpty(headerValue)) {
            // development 配置不完整，执行验证码验证
            return false;
        }

        final String inValue = headers.getExtension(headerName);
        if (StrUtil.isEmpty(inValue)) {
            // 请求头中缺少指定 header，执行验证码验证
            return false;
        }

        // header 值匹配，跳过验证码验证
        return inValue.equals(headerValue);
    }

    @Address(Addr.API_AUTH_CAPTCHA)
    public Future<JsonObject> captcha(final User user) {
        if (Objects.nonNull(user)) {
            // 80216 已登录用户不允许获取验证码
            final String id = Account.userId(user);
            return Fx.failOut(_80216Exception403CaptchaProfile.class, id);
        }
        return this.captchaStub.generate();
    }
}
