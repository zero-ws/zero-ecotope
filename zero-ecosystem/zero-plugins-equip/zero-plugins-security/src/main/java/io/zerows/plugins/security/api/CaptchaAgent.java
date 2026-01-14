package io.zerows.plugins.security.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * 安全插件标准化基础接口
 * <pre>
 *     1. Basic认证无需登录
 *     2. 内置账号登录
 *        POST /auth/login
 *     3. 图片验证码
 *        GET  /auth/captcha
 *     4. 图片验证码直接在登录接口中可直接追加使用（启用禁用取决于配置项）
 * </pre>
 */
@EndPoint
public interface CaptchaAgent {
    /**
     * 获取图片验证码
     * <pre>
     *     {
     *         "captchaId": "????",
     *         "image": "data:image/png;base64,XXXX"
     *     }
     * </pre>
     *
     * @return 响应结果
     */
    @GET
    @Path("/auth/captcha")
    @Address(Addr.API_AUTH_CAPTCHA)
    JsonObject captcha();
}
