package io.zerows.extension.commerce.rbac.agent.api.login;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.commerce.rbac.eon.Addr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 新增扩展登录流程
 * <pre><code>
 *     1. wechat / 微信登录
 *        /wechat/qr
 *     2. mobile / 短信登录
 *        /mobile/login
 *        /mobile/send  ( mobile )
 * </code></pre>
 *
 * @author lang : 2024-07-07
 */
@EndPoint
public interface ExtensionAgent {

    @POST
    @Path("/mobile/send")
    @Address(Addr.Auth.Extension.SMS_CAPTCHA)
    JsonObject generateSms(@BodyParam JsonObject params);

    @POST
    @Path("/mobile/login")
    @Address(Addr.Auth.Extension.SMS_LOGIN)
    JsonObject loginSms(@BodyParam JsonObject params);

    @POST
    @Path("/wechat/send")
    @Address(Addr.Auth.Extension.WECHAT_QR)
    JsonObject generateWechat(@BodyParam JsonObject params);

    @POST
    @Path("/wechat/login")
    @Address(Addr.Auth.Extension.WECHAT_LOGIN)
    JsonObject loginWechat(@BodyParam JsonObject params);
}
