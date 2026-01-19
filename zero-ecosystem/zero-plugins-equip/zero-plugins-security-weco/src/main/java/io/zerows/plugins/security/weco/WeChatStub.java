package io.zerows.plugins.security.weco;

import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 微信公众号 (WeChat Official Account) 业务服务
 * <p>
 * 职责：
 * 1. 管理公众号的 OAuth2 授权流程
 * 2. 校验回调 Code 并提取用户身份
 * </p>
 *
 * @author lang : 2025-12-09
 */
public interface WeChatStub {

    // ==========================================
    // 模式一：OAuth2 网页授权 (手机微信内使用)
    // ==========================================

    /**
     * 获取微信扫码登录 URL
     *
     * @param redirectUri 回调地址
     * @param state       状态参数
     * @return 包含 URL 的结果对象
     */
    Future<JsonObject> getAuthUrl(String redirectUri, String state);

    /**
     * 校验登录请求
     *
     * @param request 包含 Code 的请求对象
     * @return 填充了 OpenID 的完整请求对象
     */
    Future<WeChatReqPreLogin> validate(WeChatReqPreLogin request);

    // ==========================================
    // 模式二：扫码登录 (PC端/非微信环境使用)
    // ==========================================

    /**
     * 获取登录二维码
     *
     * @return 登录二维码获取
     */
    Future<JsonObject> getQrCode();

    /**
     * 检查扫码状态（前端轮询检查）
     *
     * @param uuid 扫码会话 ID
     * @return 包含 status 的结果对象
     */
    Future<JsonObject> checkStatus(String uuid);

    /**
     * 微信配置过程中的回调检查（现阶段只有公众号需要此功能）
     *
     * @param params 回调参数
     * @return 是否验证通过
     */
    boolean checkEcho(WeArgsSignature params);


    // ==========================================
    // 根据参数提取用户数据
    // ==========================================
    Future<JsonObject> extract(String uuid, WeArgsCallback parameter);
}
