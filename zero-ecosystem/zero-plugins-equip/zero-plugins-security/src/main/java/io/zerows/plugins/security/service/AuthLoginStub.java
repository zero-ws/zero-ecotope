package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;

import java.time.Duration;

/**
 * <pre>
 * 🟢 登录认证标准化接口 (SPI)
 *
 * 1. 🌐 全局说明
 *    定义了系统支持的多种登录认证方式的标准行为契约。
 *    每个实现类对应一种特定的认证策略 (Strategy Pattern)。
 *
 * 2. 🧬 两阶段认证设计
 *    为了支持复杂的认证流程（如验证码、短信发送），设计了两个核心步骤：
 *    - Phase 1: {@link #authorize(LoginRequest, Duration)} - 预授权/准备阶段。
 *      (e.g., 发送短信验证码、生成图形验证码、获取 OAuth2 跳转链接)
 *    - Phase 2: {@link #login(LoginRequest)} - 认证执行阶段。
 *      (e.g., 校验账号密码、校验短信验证码、交换 OAuth2 Token)
 *
 * 3. 🧩 典型实现
 *    - Password: 账号密码登录 (authorize=验证码, login=密码校验)
 *    - SMS:      短信登录 (authorize=发短信, login=验证码校验)
 *    - Email:    邮件登录 (authorize=发邮件, login=验证码校验)
 *    - OAuth:    三方登录 (authorize=无, login=Code换Token)
 * </pre>
 *
 * @author lang
 */
public interface AuthLoginStub {
    /**
     * <pre>
     * 🟢 阶段一：预授权/验证码分发
     *
     * 1. 🌐 使用场景
     *    在正式提交登录凭证之前，执行必要的准备工作或验证码发送。
     *    接口对应关系表：
     *    +---------------------+-----------------------------+--------------------------+
     *    | 认证方式 (Type)      | 前置接口 (URI)               | 说明                      |
     *    +---------------------+-----------------------------+--------------------------+
     *    | sms                 | /auth/sms-send              | 发送短信验证码             |
     *    | email               | /auth/email-send            | 发送邮件验证码             |
     *    | ldap                | (无)                        | LDAP 通常不需要预授权      |
     *    | wecom               | /auth/wecom-qrcode          | 获取企业微信登录二维码参数  |
     *    | password            | /auth/captcha               | 获取图形验证码 (若开启)     |
     *    +---------------------+-----------------------------+--------------------------+
     *
     * 2. 🎯 典型行为
     *    - SMS/Email: 生成随机验证码，并通过网关发送给用户。
     *    - Password: 生成图形验证码 (Captcha) 并缓存。
     *    - WeCom/DingTalk: 生成登录二维码所依赖的参数。
     *
     * 3. ⚙️ 返回值说明
     *    返回一个标识符字符串，具体的含义取决于实现：
     *    - 验证码 ID (Captcha ID)
     *    - 业务流水号 (Transaction ID)
     *    - 空字符串 (如果无需返回数据)
     * </pre>
     *
     * @param request   登录请求参数 (包含手机号、邮箱、账号等)
     * @param expiredAt 预授权/验证码的有效期 (TTL)
     * @return 异步结果，包含预授权标识或相关数据
     */
    default Future<String> authorize(final LoginRequest request, final Duration expiredAt) {
        return Future.succeededFuture();
    }

    /**
     * <pre>
     * 🟢 阶段二：执行认证登录
     *
     * 1. 🌐 使用场景
     *    用户提交最终的认证凭据（密码、验证码、Ticket），系统执行身份校验。
     *    接口对应关系表：
     *    +---------------------+-----------------------------+
     *    | 认证方式 (Type)      | 执行接口 (URI)               |
     *    +---------------------+-----------------------------+
     *    | sms                 | /auth/sms-login             |
     *    | email               | /auth/email-login           |
     *    | ldap                | /auth/ldap-login            |
     *    | wecom               | /auth/wecom-login           |
     *    | password            | /auth/login                 |
     *    +---------------------+-----------------------------+
     *
     * 2. 🎯 核心逻辑
     *    - 校验凭据的有效性 (密码匹配、验证码正确且未过期)。
     *    - 检索用户信息 (User Profile)。
     *    - 构造并返回标准的用户会话对象 {@link UserAt}。
     *
     * 3. 🛡️ 安全规范
     *    - 认证失败应抛出 {@link io.r2mo.typed.exception.WebException} (如 401)。
     *    - 成功登录后系统将自动签发 Token。
     * </pre>
     *
     * @param request 登录请求参数 (包含密码、验证码、Code 等)
     * @return 异步结果，包含已认证的用户会话信息
     */
    Future<UserAt> login(LoginRequest request);
}
