package io.zerows.plugins.security.service;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.Credentials;
import lombok.Getter;

/**
 * <pre>
 * 🟢 异步用户凭证 (Carrier Credential)
 *
 * 1. 🌐 作用
 * 用于在 Gateway 和 Provider 之间传递一个 **已经实例化且已认证** 的 {@link User} 对象。
 * 通常用于 Extension (如 AES, OAuth2) 已经完成了复杂的异步认证过程，直接将结果传递给下游。
 *
 * 2. 场景
 * - Extension 解析 Token 并查库/查缓存成功，拿到了 User。
 * - 需要复用 Gateway 的 `authenticate(Credentials)` 流程，而不是直接返回 User。
 * </pre>
 */
@DataObject
public class AsyncUserCredentials implements Credentials {

    @Getter
    private final User user;

    /**
     * 构造函数
     *
     * @param user 已认证的 User 对象
     */
    public AsyncUserCredentials(final User user) {
        // 构造时不抛出异常，留给 checkValid 进行校验
        this.user = user;
    }

    /**
     * <pre>
     * 转换为 JSON
     * 返回 User 的 principal 数据，用于审计日志或序列化
     * </pre>
     */
    @Override
    public JsonObject toJson() {
        if (this.user == null) {
            return new JsonObject();
        }
        return this.user.principal();
    }

    /**
     * <pre>
     * 校验有效性
     * 确保内部的 User 对象存在且 Principal 不为空。
     * </pre>
     *
     * @param arg 校验参数 (通常为 null)
     * @throws CredentialValidationException 校验失败时抛出，带 [ ZERO ] 前缀
     */
    @Override
    public <V> void checkValid(final V arg) throws CredentialValidationException {
        if (this.user == null) {
            throw new CredentialValidationException("[ ZERO ] ( Security ) 异步用户实例丢失，无法构建凭证！");
        }
        if (this.user.principal() == null) {
            throw new CredentialValidationException("[ ZERO ] ( Security ) 用户身份数据 (Principal) 为空，凭证无效！");
        }
    }

    @Override
    public String toString() {
        return "AsyncUserCredentials{principal=" + (this.user != null ? this.user.principal() : "null") + "}";
    }
}