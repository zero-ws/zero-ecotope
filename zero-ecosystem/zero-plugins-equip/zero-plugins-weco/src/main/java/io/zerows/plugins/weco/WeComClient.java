package io.zerows.plugins.weco;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.sdk.plugins.AddOn;

/**
 * 企业微信 (WeCom) 业务客户端
 * <p>
 * 定义标准化的企业微信 OAuth2 交互行为。
 * </p>
 *
 * @author lang : 2025-12-09
 */
@AddOn.Name("DEFAULT_WECOM_CLIENT")
public interface WeComClient {
    static WeComClient createClient(final Vertx vertx, final WeCoConfig config) {
        return new WeComClientImpl(vertx, config);
    }

    /**
     * 获取企业微信扫码登录 URL
     *
     * @param redirectUri 回调地址 (需在企微应用后台配置可信域名)
     * @param state       防伪随机串
     * @return 包含 url 的响应对象
     */
    Future<JsonObject> authUrl(String redirectUri, String state);

    /**
     * 执行登录：使用 Code 换取企业成员信息
     *
     * @param code 企微回调的临时授权码
     * @return 包含 UserID (企业成员ID) 或 OpenID (非企业成员) 的响应对象
     */
    Future<JsonObject> login(String code);

    /**
     * 获取扫码登录二维码 (SSO URL)
     *
     * @return 包含 qrUrl, uuid 等信息的响应对象
     */
    Future<JsonObject> qrCode(String state);

    /**
     * 检查扫码状态
     *
     * @param uuid 扫码会话 ID
     * @return 包含 status (WAITING/SUCCESS/EXPIRED) 和 userId (如果成功) 的响应对象
     */
    Future<JsonObject> checkStatus(String uuid);
}
