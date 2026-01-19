package io.zerows.plugins.security.weco;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 微信公众号 (WeChat) 认证控制器
 *
 * @author lang : 2025-12-09
 */
@EndPoint
public interface ApiWeMpAgent {
    // ==========================================
    // 模式二：扫码登录 (PC端/非微信环境使用)
    // ==========================================

    /**
     * 获取微信扫码登录二维码
     * <p>GET /auth/wechat-qrcode</p>
     */
    @GET
    @Path("/auth/wechat-qrcode")
    @Address(ApiAddr.API_AUTH_WEMP_QRCODE)
    JsonObject qrCode();


    /**
     * 检查扫码状态
     * <p>POST /auth/wechat-status</p>
     *
     * @param params 请求参数 { "uuid": "..." }
     */
    @POST
    @Path("/auth/wechat-status")
    @Address(ApiAddr.API_AUTH_WEMP_STATUS)
    JsonObject status(@BodyParam JsonObject params);
}
