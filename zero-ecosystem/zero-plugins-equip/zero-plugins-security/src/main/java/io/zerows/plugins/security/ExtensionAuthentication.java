package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * <pre>
 * 🟢 认证与凭证提取 SPI 接口
 *
 * 1. 🌐 全局说明
 *    定义了第三方或自定义认证扩展的标准协议接口。
 *    通过 Service Provider Interface (SPI) 机制，允许开发者插入自定义的认证逻辑。
 *
 * 2. 🎯 核心作用
 *    - 策略识别：通过 `support()` 方法判断是否支持当前的 Authorization 头。
 *    - 凭证解析：通过 `resolve()` 方法将请求头解析为 AuthenticationResult。
 *
 * 3. 🧩 典型实现
 *    - BasicAuth: 解析 "Basic base64(...)" -> UsernamePasswordCredentials。
 *    - BearerAuth: 解析 "Bearer token..." -> JwtUser / AESUser。
 *    - OAuth2: 解析 "Bearer token..." -> OAuth2Token -> User。
 * </pre>
 */
public interface ExtensionAuthentication {

    /**
     * <pre>
     * 🟢 定义当前 Extension 的名称
     *
     * 1. 🎯 作用
     *    - 用于标识一组 Extension（如 "basic", "jwt", "oauth2"）。
     *    - 与 SecurityMeta.type 进行匹配，找到对应的配置元数据。
     * </pre>
     *
     * @return 组件唯一名称
     */
    String name();

    /**
     * <pre>
     * 🟢 判断是否支持当前 Authorization 头
     *
     * 1. 🌐 使用场景
     *    在 AuthenticatorHandlerGateway 中被调用，用于筛选合适的处理器。
     *
     * 2. 🎯 匹配逻辑
     *    通常基于 Authorization 头的 schema 前缀进行判断，例如：
     *    - "Basic " -> 返回 true
     *    - "Bearer " -> 返回 true
     *
     * 3. ⚙️ 参数与返回值
     *    @param authorization HTTP 请求头 Authorization 的完整值
     *    @return true 表示支持处理此请求，false 表示忽略
     * </pre>
     */
    boolean support(String authorization);

    /**
     * <pre>
     * 🟢 执行解析与认证
     *
     * 1. 🌐 方法说明
     *    解析输入的认证参数，生成标准化的 `ExtensionAuthenticationResult`。
     *
     * 2. 🧬 双模式返回 (Hybrid Result)
     *    此方法返回的结果包含两种可能性（进可攻，退可守）：
     *
     *    - 🅰️ 模式 A (User - 已认证):
     *      Extension 内部自行完成了所有验证（如 JWT 验签、AES 解密），
     *      直接构造并返回合法的 `User` 对象。
     *      -> 此时后续流程无需再次查库或校验密码。
     *
     *    - 🅱️ 模式 B (Credentials - 待认证):
     *      Extension 仅完成了格式解析（如 Base64 解码得到用户名/密码），
     *      构造并在返回 `Credentials` 对象。
     *      -> 此时后续流程会将 Credentials 传递给 `AuthenticationProvider` 进行最终验证。
     * </pre>
     *
     * @param input 输入参数，包含 header 等信息
     * @param vertx Vert.x 实例，用于异步操作
     * @param meta  当前安全墙的元数据配置
     * @return 异步结果，包含解析后的 User 或 Credentials
     */
    Future<ExtensionAuthenticationResult> resolve(JsonObject input, Vertx vertx, SecurityMeta meta);
}
