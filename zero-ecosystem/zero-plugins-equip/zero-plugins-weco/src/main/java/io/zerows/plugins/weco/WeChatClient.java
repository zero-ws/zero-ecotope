package io.zerows.plugins.weco;

import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.sdk.plugins.AddOn;

/**
 * 微信公众号 (WeChat) 业务客户端
 * <p>
 * 定义标准化的微信 OAuth2 交互行为。
 * </p>
 *
 * @author lang : 2025-12-09
 */
@AddOn.Name("DEFAULT_WECHAT_CLIENT")
public interface WeChatClient {

    static WeChatClient createClient(final Vertx vertx, final WeCoConfig config) {
        return new WeChatClientImpl(vertx, config);
    }

    /**
     * 获取 PC 端扫码登录的 URL (Website QR Connect)
     *
     * @param redirectUri 回调地址 (需与微信后台配置一致)
     * @param state       防伪随机串
     * @return 包含 url 的响应对象
     */
    Future<JsonObject> authUrl(String redirectUri, String state);

    /**
     * 执行登录：使用 Code 换取用户信息
     *
     * @param code 微信回调的临时授权码
     * @return 包含 OpenID、Nickname 等用户信息的响应对象
     */
    Future<JsonObject> login(String code);

    /**
     * 获取扫码登录二维码
     *
     * @return 包含 qrUrl, uuid 等信息的响应对象
     */
    Future<JsonObject> qrCode();

    /**
     * 检查扫码状态
     *
     * @param uuid 扫码会话 ID
     * @return 包含 status (WAITING/SUCCESS/EXPIRED) 和 openId (如果成功) 的响应对象
     */
    Future<JsonObject> checkStatus(String uuid);

    /**
     * 接口配置信息检查
     * <pre>
     *     {
     *         "signature": "???",
     *         "timestamp": "???",
     *         "nonce": "???"
     *     }
     * </pre>
     *
     * @param params 检查参数
     * @return 响应信息
     */
    Future<Boolean> checkEcho(WeArgsSignature params);

    /**
     * 返回用户信息，主要返回
     * <pre>
     *     - unionId
     *     - openId
     * </pre>
     *
     * @param callback 回调参数
     * @return 用户信息
     */
    Future<JsonObject> extractUser(WeArgsCallback callback);
}
