package io.zerows.plugins.security.ldap;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.internal.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.Objects;

/**
 * 🛡️ [ZERO] LDAP 搜索认证提供者
 * <p>
 * 核心逻辑：Admin Bind -> Search Filter -> Found DN -> User Re-Bind
 * </p>
 */
@Slf4j
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private static final String LOG_PREFIX = "[ ZERO ] ( LDAP ) ";
    private static final String AUTH_MODE = "Search & Bind (搜索模式)";

    private static final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String SIMPLE_AUTH = "simple";
    private static final String FOLLOW = "follow";

    private final Vertx vertx;
    private final LdapOptions options;

    public LdapAuthenticationProvider(final Vertx vertx, final LdapOptions options) {
        this.vertx = Objects.requireNonNull(vertx);
        this.options = Objects.requireNonNull(options);
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        UsernamePasswordCredentials authInfo = null;
        try {
            if (credentials instanceof final UsernamePasswordCredentials credentialsAccount) {
                credentialsAccount.checkValid(null);
                authInfo = credentialsAccount;
            }
            if (Objects.isNull(authInfo)) {
                return Future.failedFuture(new CredentialValidationException("凭证类型不匹配，仅支持用户名/密码类型"));
            }
        } catch (final RuntimeException e) {
            log.warn("{} 凭证格式校验失败: {}", LOG_PREFIX, e.getMessage());
            return Future.failedFuture(new CredentialValidationException("凭证格式无效", e));
        }

        final String username = authInfo.getUsername();
        final String password = authInfo.getPassword();

        // 检查 Admin 配置
        if (Ut.isNil(this.options.getUsername()) || Ut.isNil(this.options.getPassword())) {
            log.error("{} LDAP 配置错误: 未配置管理员账号(admin)或密码，无法执行搜索模式", LOG_PREFIX);
            return Future.failedFuture("LDAP 配置缺失: 缺少管理员信息");
        }

        return this.doSearchAndBind(username, password);
    }

    private Future<User> doSearchAndBind(final String inputId, final String inputPwd) {
        final Promise<User> promise = ((VertxInternal) this.vertx).promise();

        this.vertx.executeBlocking(() -> {
            LdapContext ctx = null;
            NamingEnumeration<SearchResult> results = null;
            try {
                // 第一步：管理员绑定
                final Hashtable<String, Object> adminEnv = this.createJndiEnv(this.options.getUsername(), this.options.getPassword());
                ctx = new InitialLdapContext(adminEnv, null);

                // 第二步：执行搜索
                final String searchBase = Ut.isNil(this.options.getBase()) ? "" : this.options.getBase();
                final String filterTemplate = this.options.getUserQuery() != null && !this.options.getUserQuery().isEmpty()
                    ? this.options.getUserQuery().getFirst()
                    : "(uid={0})";
                final String filter = filterTemplate.replace("{0}", inputId);

                final SearchControls controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                // 动态指定需要返回的属性：包含配置中的 userId 和 userEmail 字段名
                controls.setReturningAttributes(new String[]{
                    "dn", "cn", "sn", this.options.getUserId(), this.options.getUserEmail(), "description"
                });

                results = ctx.search(searchBase, filter, controls);

                if (!results.hasMore()) {
                    // 搜索失败日志
                    log.warn("{} 认证失败 | 模式: {} | 账号: [{}] | 搜索条件: [{}]", LOG_PREFIX, AUTH_MODE, inputId, filter);
                    throw new CredentialValidationException("用户不存在");
                }

                final SearchResult entry = results.next();
                final String userDn = entry.getNameInNamespace();

                // 第三步：用户密码验证
                this.verifyUserPassword(userDn, inputPwd);

                // 第四步：构造结果
                final JsonObject principal = new JsonObject();
                principal.put("username", inputId);
                principal.put("dn", userDn);

                // 根据 LdapOptions 中的配置字段名提取属性
                final Attributes attrs = entry.getAttributes();
                this.extractAttribute(attrs, this.options.getUserId(), principal, "uid");
                this.extractAttribute(attrs, this.options.getUserEmail(), principal, "mail");
                this.extractAttribute(attrs, "cn", principal, "cn");
                // ------------------ 非标准属性
                {
                    principal.put(KName.ID, inputId);
                    principal.put(KName.HABITUS, inputId);
                }

                // 认证成功日志
                log.info("{} 认证成功 | 模式: {} | 账号: [{}] | 搜索条件: [{}]", LOG_PREFIX, AUTH_MODE, inputId, filter);
                return User.create(principal);

            } catch (final NamingException e) {
                final String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("error code 49")) {
                    log.warn("{} 密码验证失败 | 模式: {} | 账号: [{}]", LOG_PREFIX, AUTH_MODE, inputId);
                    throw new CredentialValidationException("密码错误", e);
                }
                log.error("{} LDAP 通信异常: {}", LOG_PREFIX, errorMsg, e);
                throw new CredentialValidationException("LDAP 服务异常 / 通信异常", e);
            } finally {
                this.closeQuietly(results);
                this.closeQuietly(ctx);
            }
        }).onComplete(promise);

        return promise.future();
    }

    private void verifyUserPassword(final String dn, final String password) throws NamingException {
        LdapContext userCtx = null;
        try {
            final Hashtable<String, Object> userEnv = this.createJndiEnv(dn, password);
            userCtx = new InitialLdapContext(userEnv, null);
        } finally {
            this.closeQuietly(userCtx);
        }
    }

    private Hashtable<String, Object> createJndiEnv(final String principal, final String credential) {
        final Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, this.options.getUrl());

        final String mechanism = this.options.getMechanism();
        env.put(Context.SECURITY_AUTHENTICATION, Ut.isNil(mechanism) ? SIMPLE_AUTH : mechanism);

        if (principal != null) {
            env.put(Context.SECURITY_PRINCIPAL, principal);
        }
        if (credential != null) {
            env.put(Context.SECURITY_CREDENTIALS, credential);
        }

        final String referral = this.options.getReferral();
        env.put(Context.REFERRAL, Ut.isNil(referral) ? FOLLOW : referral);
        return env;
    }

    private void extractAttribute(final Attributes attrs, final String attrId, final JsonObject json, final String targetKey) throws NamingException {
        if (Ut.isNil(attrId)) {
            return;
        }
        final Attribute attr = attrs.get(attrId);
        if (attr != null && attr.get() != null) {
            json.put(targetKey, attr.get().toString());
        }
    }

    private void closeQuietly(final Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (final NamingException ignored) {
            }
        }
    }

    private void closeQuietly(final NamingEnumeration<?> results) {
        if (results != null) {
            try {
                results.close();
            } catch (final NamingException ignored) {
            }
        }
    }
}