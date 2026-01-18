package io.zerows.plugins.security;

import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;

import java.util.Objects;

/**
 * <pre>
 * 🟢 扩展认证结果封装
 *
 * 1. 🌐 全局说明
 *     用于封装 `ExtensionAuthentication.resolve()` 方法的执行结果。
 *     这是一个 Union Type（联合类型）的数据结构，表示两种不同的认证阶段状态。
 *
 * 2. 🧬 核心设计：进可攻，退可守
 *     此对象在任意时刻通常只持有一方数据（互斥或优先）：
 *
 *     - ✅ 情况 A - 终态 (User)：
 *       表示 Extension 已经完成了全套验证逻辑（如校验了 Token 签名、有效期）。
 *       此时 `user` 字段非空，`credentials` 字段通常为 null。
 *       -> Gateway 将直接放行，不再调用 AuthProvider。
 *
 *     - 🔄 情况 B - 中态 (Credentials)：
 *       表示 Extension 仅完成了协议解析（如从 Basic Header 提取了用户名/密码）。
 *       此时 `credentials` 字段非空，`user` 字段为 null。
 *       -> Gateway 将提取 Credentials，转交给 AuthProvider 进行密码验证。
 * </pre>
 */
public class ExtensionAuthenticationResult {
    private final User user;             // 也就是 "进"：Extension 已经完成了闭环验证
    private final Credentials credentials; // 也就是 "退"：Extension 只做了解析，需要后续验证

    // 私有构造，强制使用静态工厂
    private ExtensionAuthenticationResult(final User user, final Credentials credentials) {
        this.user = user;
        this.credentials = credentials;
    }

    /**
     * <pre>
     * 🏭 静态工厂：绑定已认证用户
     *
     * 1. 🌐 使用场景
     *    当 Extension (如 JWT/AES) 成功验证了令牌并生成了 User 对象时使用。
     * </pre>
     *
     * @param user 已认证通过的 Vert.x User 对象
     * @return 包含 User 的结果对象
     */
    public static ExtensionAuthenticationResult bindAsync(final User user) {
        return new ExtensionAuthenticationResult(user, null);
    }

    /**
     * <pre>
     * 🏭 静态工厂：绑定待验证凭证
     *
     * 1. 🌐 使用场景
     *    当 Extension (如 BasicAuth) 仅提取了凭证信息，需要后续步骤进行验证时使用。
     * </pre>
     *
     * @param credentials 解析出的待验证凭证
     * @return 包含 Credentials 的结果对象
     */
    public static ExtensionAuthenticationResult bindAsync(final Credentials credentials) {
        return new ExtensionAuthenticationResult(null, credentials);
    }

    /**
     * <pre>
     * 🟢 判断是否已完成验证
     *
     * 1. 🎯 作用
     *    网关根据此标记决定后续流程。
     *    - true: 认证结束，设置 Security Context。
     *    - false: 认证未结束，调用 AuthenticatorProvider。
     * </pre>
     *
     * @return 如果包含有效的 User 对象则返回 true
     */
    public boolean isVerified() {
        return Objects.nonNull(this.user);
    }

    // Getters
    public User getUser() {
        return this.user;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }
}
