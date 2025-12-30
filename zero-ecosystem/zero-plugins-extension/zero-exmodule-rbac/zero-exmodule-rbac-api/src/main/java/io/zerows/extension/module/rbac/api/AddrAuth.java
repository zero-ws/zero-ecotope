package io.zerows.extension.module.rbac.api;

/**
 * 新版安全基础规范模块
 * <pre>
 *     1. 基础登录地址
 *        - POST /auth/login
 *        - POST /auth/login-jwt
 *     2. 图片验证码获取
 *        - GET  /auth/captcha
 *     3. Email登录
 *        - POST /auth/email-send
 *        - POST /auth/email-login
 *     4. LDAP登录
 *        - POST /auth/ldap-login
 *     5. 短信登录
 *        - POST /auth/sms-send
 *        - POST /auth/sms-login
 *     6. 微信和企微
 *        微信公众号
 *        - GET  /auth/wechat-qrcode
 *        - POST /auth/wechat-status
 *        - GET  /auth/wechat-callback
 *        - POST /auth/wechat-callback
 *        企微登录
 *        - GET  /auth/wecom-init
 *        - GET  /auth/wecom-login
 *        - GET  /quth/wecom-qrcode
 * </pre>
 *
 * @author lang : 2025-12-30
 */
public interface AddrAuth {
    String LOGIN = Prefix._EVENT + "O-LOGIN";
}
