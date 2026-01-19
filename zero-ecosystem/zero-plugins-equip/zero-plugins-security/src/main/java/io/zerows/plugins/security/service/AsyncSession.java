package io.zerows.plugins.security.service;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Account;
import io.zerows.support.Ut;
import lombok.Getter;

import java.util.Objects;

/**
 * <pre>
 * 🟢 异步会话凭证 (Unified Async Session)
 *
 * 1. 🌐 全局说明
 * Zero Security 的核心交互对象。
 * 它是 Extension、Gateway 和 Provider 之间传递 "用户身份 + 会话ID" 的标准载体。
 *
 * 2. 🧬 核心变更
 * - Session 对象被替换为 `String sessionId`，实现 Web 层与 Security 层的彻底解耦。
 * </pre>
 */
@DataObject
public class AsyncSession implements Credentials {

    @Getter
    private final User user;

    @Getter
    private final Credentials credentials;

    /**
     * Web 会话 ID (不再持有 Session 对象)
     */
    @Getter
    private final String sessionId;
    private String authorization;

    private AsyncSession(final User user, final Credentials credentials, final String sessionId) {
        this.user = user;
        this.credentials = credentials;
        this.sessionId = sessionId;
    }

    /**
     * 🏭 绑定：已认证用户 + 会话ID (终态)
     */
    public static AsyncSession bindAsync(final User user, final String authorization) {
        final String sessionId = Account.userId(user);
        return new AsyncSession(user, null, sessionId).setAuthorization(authorization);
    }

    // ==================== 私有构造 ====================

    /**
     * 🏭 绑定：待验证凭证 + 会话ID (中态)
     */
    public static AsyncSession bindAsync(final Credentials credentials, final String sessionId) {
        return new AsyncSession(null, credentials, sessionId);
    }

    // ==================== 静态工厂 (bindAsync 重载) ====================

    public static AsyncSession bindAsync(final Credentials credentials, final JsonObject params) {
        final String sessionId = Ut.valueString(params, KName.SESSION);
        return new AsyncSession(null, credentials, sessionId);
    }

    /**
     * 特殊说明，为了保证 JWT, AES 等所有的 Token 使用的 session 不一致而不造成混用，所以此处对 sessionId 进行一次干扰
     *
     * @param authorization HTTP Authorization 头
     * @return 当前实例
     */
    private AsyncSession setAuthorization(final String authorization) {
        this.authorization = authorization;
        return this;
    }

    // ==================== 逻辑判断 ====================

    public boolean isVerified() {
        return Objects.nonNull(this.user);
    }

    // ==================== Credentials 接口实现 ====================

    @Override
    public JsonObject toJson() {
        final JsonObject data;
        if (this.user != null) {
            data = this.user.principal();
        } else if (this.credentials != null) {
            data = this.credentials.toJson();
        } else {
            data = new JsonObject();
        }

        // 🟢 自动注入 Session ID 字符串
        if (this.sessionId != null) {
            data.put(KName.SESSION, this.sessionId);
        }
        return data;
    }

    @Override
    public <V> void checkValid(final V arg) throws CredentialValidationException {
        if (this.isVerified()) {
            if (this.user == null || this.user.principal() == null) {
                throw new CredentialValidationException("[ ZERO ] ( Security ) 异步会话中用户实例丢失！");
            }
        } else {
            if (this.credentials != null) {
                this.credentials.checkValid(arg);
            } else {
                throw new CredentialValidationException("[ ZERO ] ( Security ) 异步会话中凭证数据丢失！");
            }
        }
    }

    @Override
    public String toHttpAuthorization() {
        if (Objects.nonNull(this.credentials)) {
            return this.credentials.toHttpAuthorization();
        }
        if (this.isVerified()) {
            return this.authorization;
        }
        return null;
    }

    @Override
    public String toString() {
        return "AsyncSession{" +
            "user=" + this.user +
            ", credentials=" + this.credentials +
            ", sessionId='" + this.sessionId + '\'' +
            ", authorization='" + this.authorization + '\'' +
            '}';
    }
}