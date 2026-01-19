package io.zerows.plugins.security.weco;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public class ApiWeMpCallback {

    @Inject
    private WeChatService weChatService;

    @GET
    @Path("/auth/wechat-callback")
    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_GET)
    public JsonObject callbackGet(@QueryParam("signature") final String signature,
                                  @QueryParam("timestamp") final String timestamp,
                                  @QueryParam("nonce") final String nonce,
                                  @QueryParam("echostr") final String echostr) {
        final JsonObject exchangeJ = new JsonObject();
        exchangeJ.put("signature", signature);
        exchangeJ.put("timestamp", timestamp);
        exchangeJ.put("nonce", nonce);
        exchangeJ.put("echostr", echostr);
        return exchangeJ;
    }

    /**
     * 接收微信回调事件 (扫码、关注等)
     * <p>POST /auth/wechat-callback</p>
     * 注意：
     * 1. 微信发送的是 XML，不能用 JObject 接收
     * 2. 必须返回 "success" 纯文本，不能返回 JSON
     */
    @POST
    @Path("/auth/wechat-callback")
    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_POST)
    public JsonObject callbackPost(@BodyParam final String body,
                                   @QueryParam("signature") final String signature,
                                   @QueryParam("timestamp") final String timestamp,
                                   @QueryParam("nonce") final String nonce,
                                   @QueryParam("encrypt_type") final String encType,
                                   @QueryParam("msg_signature") final String msgSignature
    ) {
        final JsonObject exchangeJ = new JsonObject();
        exchangeJ.put("body", body);
        exchangeJ.put("signature", signature);
        exchangeJ.put("timestamp", timestamp);
        exchangeJ.put("nonce", nonce);
        exchangeJ.put("encrypt_type", encType);
        exchangeJ.put("msg_signature", msgSignature);
        return exchangeJ;
    }
}
