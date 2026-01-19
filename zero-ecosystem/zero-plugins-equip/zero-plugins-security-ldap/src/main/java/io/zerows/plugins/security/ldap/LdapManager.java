package io.zerows.plugins.security.ldap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.ldap.LdapAuthentication;
import io.vertx.ext.auth.ldap.LdapAuthenticationOptions;
import io.zerows.plugins.security.SecurityChainAuth;
import io.zerows.support.Ut;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 🛡️ [ZERO] LDAP 认证管理器
 * <p>
 * 职责：
 * 1. 解析 LDAP 配置 (LdapOptions)。
 * 2. 编排认证策略：根据 user-query 数组的长度，构建 ChainAuth 责任链。
 * 3. 决定认证模式：存在 Admin 账号时使用 SearchProvider，否则使用原生 Direct Bind。
 * </p>
 */
@Slf4j
public class LdapManager {

    private static final String LOG_PREFIX = "[ ZERO ] ( LDAP ) ";
    private static final Cc<Integer, LdapManager> CC_MANAGER = Cc.open();

    private final Vertx vertxRef;
    // 保存最终合并后的配置供参考
    private final JsonObject finalOptions = new JsonObject();
    // 缓存构建好的聚合 Provider
    @Getter
    private AuthenticationProvider provider;

    private LdapManager(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    public static LdapManager of(final Vertx vertxRef) {
        return CC_MANAGER.pick(() -> new LdapManager(vertxRef), System.identityHashCode(vertxRef));
    }

    /**
     * 初始化或获取 LDAP 认证提供者
     *
     * @param inputOptions 原始配置 JsonObject (通常来自 yaml: ldap.options)
     * @return 编排好的 AuthenticationProvider (可能是 ChainAuth)
     */
    public AuthenticationProvider createProvider(final JsonObject inputOptions) {
        if (Objects.nonNull(this.provider)) {
            return this.provider;
        }

        log.info("{} 开始初始化 LDAP 认证管理器...", LOG_PREFIX);

        // 1. 强类型转换配置，处理 kebab-case 到 camelCase 的映射
        final LdapOptions globalOpts = new LdapOptions(inputOptions);

        // 2. 提取查询模板列表 (user-query / authenticationQuery)
        // 如果未配置，提供一个默认值以防报错
        List<String> queries = globalOpts.getUserQuery();
        if (queries == null || queries.isEmpty()) {
            log.warn("{} 未检测到 user-query 配置，使用默认值: (uid={0})", LOG_PREFIX);
            queries = new ArrayList<>();
            queries.add("(uid={0})");
        }

        // 3. 创建责任链 (ChainAuth)
        // 无论是一个 query 还是多个，统一用 ChainAuth 封装，保持行为一致性
        final ChainAuth chainAuth = SecurityChainAuth.any();

        // 4. 判断是否启用 "搜索模式" (Search & Bind)
        // 依据：配置中是否存在 admin 和 password
        final boolean enableSearch = Ut.isNotNil(globalOpts.getUsername()) && Ut.isNotNil(globalOpts.getPassword());

        if (enableSearch) {
            log.info("{} 检测到管理员账号，启用 [搜索并绑定] 模式 (Search & Bind)", LOG_PREFIX);
        } else {
            log.info("{} 未检测到管理员账号，启用 [直接绑定] 模式 (Direct Bind)", LOG_PREFIX);
        }

        // 5. 循环编排：为每个查询模板创建一个独立的 Provider
        int idx = 0;
        for (final String query : queries) {
            idx++;
            if (enableSearch) {
                // 🟢 策略 A: 添加自定义搜索 Provider
                // 需要构造一份"单查询"的配置给 Provider，避免 Provider 内部再做多余逻辑
                final JsonObject singleOptJson = globalOpts.toJson();
                // 强制覆盖 user-query 为当前这一个
                singleOptJson.put("user-query", new JsonArray().add(query));

                final LdapOptions singleOpts = new LdapOptions(singleOptJson);

                // 实例化 LdapSearchProvider
                final AuthenticationProvider searchProvider = new LdapAuthenticationProvider(this.vertxRef, singleOpts);
                chainAuth.add(searchProvider);

                log.info("{}  -> 策略链 [{}] (Search): 过滤器模板 = {}", LOG_PREFIX, idx, query);

            } else {
                // 🟡 策略 B: 添加原生直接绑定 Provider
                // 使用 Converter 生成纯净的原生配置 (只包含 url, mechanism, referral, authenticationQuery)
                final JsonObject nativeJson = LdapOptionsConverter.toNativeOption(globalOpts, query);
                final LdapAuthenticationOptions nativeOpts = new LdapAuthenticationOptions(nativeJson);

                // 实例化原生 Provider
                final AuthenticationProvider directProvider = LdapAuthentication.create(this.vertxRef, nativeOpts);
                chainAuth.add(directProvider);

                log.info("{}  -> 策略链 [{}] (Direct): 绑定模板 = {}", LOG_PREFIX, idx, query);
            }
        }

        this.provider = chainAuth;
        this.finalOptions.mergeIn(inputOptions, true);

        log.info("{} LDAP 认证管理器初始化完成，共加载 {} 个策略节点。", LOG_PREFIX, queries.size());

        return this.provider;
    }
}