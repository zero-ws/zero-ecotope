package io.zerows.plugins.security.weco;


import io.r2mo.xync.weco.wecom.WeComIdentify;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 企业微信 (WeCom) 业务服务
 * <p>
 * 职责：
 * 1. 管理企业微信的 OAuth2 授权流程
 * 2. 处理企业成员身份校验
 * </p>
 *
 * @author lang : 2025-12-09
 */
public interface WeComStub {
    /**
     * 初始化登录请求
     *
     * @param targetUrl 目标转发的 URL
     * @return 返回带有 state 的初始化结果
     */
    Future<WeComIdentify> initialize(String targetUrl);

    /**
     * 校验登录请求
     *
     * @param request 包含 Code 的请求对象
     * @return 填充了 UserID 的完整请求对象
     */
    Future<WeComIdentify> validate(WeComLoginRequest request);

    /**
     * 获取登录二维码 (SSO URL)
     *
     * @return 登录二维码获取
     */
    Future<JsonObject> getQrCode(String state);
}
