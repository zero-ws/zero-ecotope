package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.htdigest.HtdigestAuth;
import io.vertx.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.ldap.LdapAuthentication;
import io.vertx.ext.auth.ldap.LdapAuthenticationOptions;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.otp.hotp.HotpAuth;
import io.vertx.ext.auth.otp.hotp.HotpAuthOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.metadata.YmSecuritySpec;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 内部 Provider 专用构造器，用于构造内部认证对应的 Provider 组件
 *
 * @author lang : 2025-10-29
 */
class AuthenticationNative {
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.open();
    private static final Cc<String, AuthenticationHandler> CC_HANDLER_401 = Cc.open();

    /**
     * 注意配置提取流程，此处配置提取流程主要是依赖 Provider / Consumer 的流程信息来处理
     * <pre>
     *     1. {@link SecurityManager} 中包含了如下信息
     *        vertx = Map<SecurityType, SecurityConfig>
     *        appId / appName = Map<SecurityType, SecurityConfig>
     *        所以提取过程中使用 {@link Vertx} 实例的 hashCode 做为第一优先级的配置提取
     *
     *     2. 根据 {@link Vertx} 实例的 hashCode 可提取所有 SecurityType = SecurityConfig 的 Map，然后根据
     *        {@link SecurityMeta#getType()} 提取配置构造内部 Provider
     * </pre>
     * 在当前环境中，映射信息如下：
     * <pre>
     *     1. {@link Vertx} 实例 x 1
     *        - configOf(SecurityType)
     *        - configOf(SecurityType, Vertx)
     *        上述两个方法等价
     *
     *     2. {@link Vertx} 实例 x N
     *        - configOf(SecurityType)              --> 默认取第一个 Vertx 实例的配置
     *        - configOf(SecurityType, Vertx)       --> 根据 Vertx 实例提取对应配置
     * </pre>
     *
     * @param vertxRef Vertx 实例
     * @param meta     安全元信息
     *
     * @return 认证提供者
     */
    @SuppressWarnings("unchecked")
    static <T extends AuthenticationProvider> T createProvider(final Vertx vertxRef, final SecurityMeta meta) {
        return (T) CC_PROVIDER_401.pick(() -> {
            final SecurityConfig config = SecurityManager.of().configOf(meta.getType(), vertxRef);
            return createProvider(vertxRef, config);
        }, meta.id(vertxRef));
    }

    static AuthenticationHandler createHandler(final Vertx vertxRef, final SecurityMeta meta) {
        return CC_HANDLER_401.pick(() -> {
            final SecurityConfig config = SecurityManager.of().configOf(meta.getType(), vertxRef);
            return createHandler(vertxRef, config);
        }, meta.id(vertxRef));
    }

    /**
     * 内置 Handler 构造器
     * <pre>
     *     1. {@link SecurityType#JWT}            --> {@link JWTAuthHandler}
     *        {@link SecurityType#OAUTH2}         --> {@link OAuth2AuthHandler}
     *     2. 其他类型的的处理
     * </pre>
     *
     * @param vertxRef Vertx 实例
     * @param config   安全配置
     *
     * @return 认证处理器
     */
    private static AuthenticationHandler createHandler(final Vertx vertxRef, final SecurityConfig config) {
        if (Objects.isNull(config)) {
            return null;
        }
        final JsonObject options = config.options();
        if (SecurityType.JWT == config.type()) {
            final String realm = Ut.valueString(options, YmSecuritySpec.jwt.options.realm);
            final JWTAuth provider = createProvider(vertxRef, config);
            return JWTAuthHandler.create(provider, realm);
        }

        if (SecurityType.OAUTH2 == config.type()) {
            final String callback = Ut.valueString(options, YmSecuritySpec.oauth2.options.callback);
            final OAuth2Auth provider = createProvider(vertxRef, config);
            return OAuth2AuthHandler.create(vertxRef, provider, callback);
        }
        return null;
    }

    /**
     * 内置 Provider 构造器
     * <pre>
     *     1. {@link SecurityType#BASIC}            --> No Provider
     *        {@link SecurityType#ABAC}             --> No Provider
     *     2. {@link SecurityType#JWT}              --> {@link JWTAuth}
     *     3. {@link SecurityType#OAUTH2}           --> {@link OAuth2Auth}
     *     4. {@link SecurityType#HT_DIGEST}        --> {@link HtdigestAuth}
     *     5. {@link SecurityType#HT_PASSWD}        --> {@link HtpasswdAuth}
     *     6. {@link SecurityType#OTP}              --> {@link HotpAuth}
     *     7. {@link SecurityType#LDAP}             --> {@link LdapAuthentication}
     * </pre>
     * 官方原生的另外两种 SQL Client 和 MongoDB 的 Provider 不在此处实现，原因如下：Zero 提供了内置自定义的部分 Provider，且多数都是
     * 需要访问数据库的，所以原生类型的 Provider 就不在此处再实现了，避免冗余和重复。
     *
     * @param vertxRef Vertx 实例
     * @param config   安全配置
     *
     * @return 认证提供者
     */
    @SuppressWarnings("unchecked")
    private static <T extends AuthenticationProvider> T createProvider(final Vertx vertxRef, final SecurityConfig config) {
        if (Objects.isNull(config)) {
            return null;
        }
        final JsonObject options = config.options();
        if (SecurityType.JWT == config.type()) {
            final JWTAuthOptions jwtOptions = new JWTAuthOptions(options);
            return (T) JWTAuth.create(vertxRef, jwtOptions);
        }

        if (SecurityType.OAUTH2 == config.type()) {
            final OAuth2Options oAuth2Options = new OAuth2Options(options);
            return (T) OAuth2Auth.create(vertxRef, oAuth2Options);
        }

        if (SecurityType.HT_DIGEST == config.type()) {
            String filename = Ut.valueString(options, YmSecuritySpec.htdigest.options.filename);
            filename = Ut.isNil(filename) ? HtdigestAuth.HTDIGEST_FILE : filename;
            return (T) HtdigestAuth.create(vertxRef, filename);
        }

        if (SecurityType.HT_PASSWD == config.type()) {
            final HtpasswdAuthOptions htpasswdOptions = new HtpasswdAuthOptions(options);
            return (T) HtpasswdAuth.create(vertxRef, htpasswdOptions);
        }

        if (SecurityType.OTP == config.type()) {
            final HotpAuthOptions otpOptions = new HotpAuthOptions(options);
            return (T) HotpAuth.create(otpOptions);         // 不依赖 Vertx 实例
        }

        if (SecurityType.LDAP == config.type()) {
            final LdapAuthenticationOptions ldapOptions = new LdapAuthenticationOptions(options);
            return (T) LdapAuthentication.create(vertxRef, ldapOptions);
        }
        /*
         * ABAC / BASIC 类型不需要 Provider，返回 null 即可
         */
        return null;
    }
}
