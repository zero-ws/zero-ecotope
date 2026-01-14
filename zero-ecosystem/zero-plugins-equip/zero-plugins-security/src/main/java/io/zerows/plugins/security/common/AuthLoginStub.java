package io.zerows.plugins.security.common;

import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.UserLoginRequest;

public interface AuthLoginStub {

    /**
     * <pre>
     * 🔐 验证码校验（业务服务）
     *
     * 🎯 作用：
     * 在用户登录流程中，对图形验证码进行预检。
     * 该方法为了逻辑重用而设计，通常在 {@link AuthLoginStub} 的实现中被调用。
     *
     * ⚡️ 执行逻辑：
     * 1. 检查系统配置是否启用了验证码功能。
     * 2. 🟢 启用：提取请求中的验证码信息，比对服务端存储的值。
     *    - 成功：继续执行。
     *    - 失败：抛出验证码错误异常。
     * 3. 🔴 未启用：直接跳过校验，原样返回请求对象。
     *
     * ⚙️ 场景说明：
     * 仅在安全配置要求图形验证码时生效，保护登录接口免受暴力破解。
     * </pre>
     *
     * @param request 包含用户登录信息（账号、密码、验证码等）的请求对象
     * @return 校验通过后的 {@link UserLoginRequest} 对象（通常直接返回原对象）
     */
    Future<UserLoginRequest> validateCaptcha(UserLoginRequest request);

    Future<UserAt> login(UserLoginRequest request);
}
