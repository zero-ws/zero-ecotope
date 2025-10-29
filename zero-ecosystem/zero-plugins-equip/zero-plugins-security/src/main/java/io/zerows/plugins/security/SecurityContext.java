package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.basicore.YmSecurity;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.metadata.security.KSecurityExecutor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;
import io.zerows.specification.configuration.HSetting;
import lombok.extern.slf4j.Slf4j;

/**
 * 安全上下文，构造安全扫描等相关环境，安全上下文和App执行器结合使用
 * <pre>
 *     1. 安全上下文位于如下配置中
 *        vertx:
 *           security:
 *     2. 每个应用会拥有一份 {@link HSetting} 的基础配置，其中包含了 SECURITY 配置
 *     3. 每个应用只拥有一份 {@link SecurityMeta} 安全上下文环境
 *        /xxx-01 -> Provider-01
 *        /xxx-02 -> Provider-02
 *        /xxx-03 -> Provider-03
 *        ...
 *     4. 一个应用多个配置的场景下只能使用
 *        路径 => 安全上下文
 *               /xxxx 每个路径下 Provider 可选择多个
 * </pre>
 * 平台本身的安全设置由加载的 Maven 配置来定，只要加载的配置中包含了 security 插件即可启用以及支持，其中包括
 * <pre>
 *     - vertx-auth-oauth2
 *     - vertx-auth-jwt
 *     - vertx-auth-abac
 *     - vertx-auth-otp
 *     - vertx-auth-ldap
 *     - vertx-auth-htpasswd
 *     - vertx-auth-htdigest
 *
 *     下边两个关于存储的位置不放在此处考虑，根据实际 APP 的情况而定
 *     - vertx-auth-sql-client
 *     - vertx-auth-mongo
 * </pre>
 * 其中每一个应用都会挂载多份安全基础配置
 * <pre>
 *     1. App ->
 *            {@link SecurityMeta} / {@link KSecurityExecutor}
 *               Provider x N
 *                 - and / or 逻辑关系
 *               wall = /api/**
 *                      - type = JWT
 *                      - type = OAUTH2
 *               wall = /admin/**
 *     2. 非自定义模式 ->
 *            {@link SecurityMeta} 只支持单一 Provider 配置，直接对接 {@link YmSecurity} 中的基础配置
 * </pre>
 *
 * @author lang : 2025-10-27
 */
@Slf4j
class SecurityContext {
    private static final MultiKeyMap<SecurityMeta> CC_META = new MultiKeyMap<>();
    private static final Cc<SecurityType, SecurityConfig> CC_CONFIG = Cc.open();

    static void scanned() {
        OCacheClass.entireValue().forEach(each -> {
            if (AuthorizationProvider.class.isAssignableFrom(each)) {
                log.info("[ ZERO ] 平台可支持的安全提供器：{}", each.getName());
            }
        });
    }
}
