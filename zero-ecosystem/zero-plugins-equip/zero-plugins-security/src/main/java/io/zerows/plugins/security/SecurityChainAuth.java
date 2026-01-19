package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.Credentials;

import java.util.ArrayList;
import java.util.List;

/**
 * 🟢 [ZERO] 增强版认证链 (Enhanced Chain Auth)
 * <p>
 * 核心改进：
 * 解决了原生 Vert.x ChainAuth 在 {@code all=false} (ANY) 模式下的异常吞没问题。
 * 当链中所有 Provider 都认证失败时，本实现会抛出<b>最后一次捕获的具体异常</b>（如“密码错误”），
 * 而不是抛出通用的 "No more providers in the auth chain"，从而避免被网关误判为 500 系统错误。
 * </p>
 *
 * @author lang
 */
public class SecurityChainAuth implements ChainAuth {

    private final List<AuthenticationProvider> providers = new ArrayList<>();
    /**
     * true  = ALL 模式 (必须全部通过)
     * false = ANY 模式 (只要一个通过) -> 默认
     */
    private final boolean all;

    private SecurityChainAuth(final boolean all) {
        this.all = all;
    }

    public static SecurityChainAuth any() {
        return new SecurityChainAuth(false);
    }

    public static SecurityChainAuth all() {
        return new SecurityChainAuth(true);
    }

    @Override
    public ChainAuth add(final AuthenticationProvider other) {
        this.providers.add(other);
        return this;
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        // 1. 基础校验
        try {
            credentials.checkValid(null);
        } catch (final CredentialValidationException e) {
            return Future.failedFuture(e);
        }

        if (this.providers.isEmpty()) {
            return Future.failedFuture("No providers in the auth chain.");
        }

        // 2. 开始递归，初始 lastError 为 null
        return this.iterate(0, credentials, null, null);
    }

    /**
     * 递归执行认证逻辑
     *
     * @param idx          当前 Provider 索引
     * @param credentials  凭证
     * @param previousUser 上一个成功的用户（仅 ALL 模式使用）
     * @param lastError    上一个失败的异常（仅 ANY 模式使用，核心改进点）
     */
    private Future<User> iterate(final int idx, final Credentials credentials, final User previousUser, final Throwable lastError) {
        // 🛑 终止条件：遍历完所有 Provider
        if (idx >= this.providers.size()) {
            if (!this.all) {
                // ANY 模式：所有都试过了，全部失败
                if (lastError != null) {
                    // 🟢 核心修正：抛出最后一次的具体异常（如 401 密码错误）
                    return Future.failedFuture(lastError);
                }
                // 理论上只有空列表会走到这
                return Future.failedFuture("No more providers in the auth chain.");
            } else {
                // ALL 模式：全部通过，返回合并后的用户
                return Future.succeededFuture(previousUser);
            }
        }

        // 🚀 执行认证
        final AuthenticationProvider provider = this.providers.get(idx);
        return provider.authenticate(credentials)
            .compose(user -> {
                if (!this.all) {
                    // ANY 模式：只要有一个成功，立即成功，不需要继续
                    return Future.succeededFuture(user);
                } else {
                    // ALL 模式：当前成功，合并用户，继续下一个
                    return this.iterate(idx + 1, credentials, previousUser == null ? user : previousUser.merge(user), null);
                }
            })
            .recover(err -> {
                if (!this.all) {
                    // ANY 模式：当前失败，尝试下一个
                    // 🟢 关键：将当前的错误 (err) 传递给下一次递归的 lastError 参数
                    return this.iterate(idx + 1, credentials, null, err);
                } else {
                    // ALL 模式：只要有一个失败，立即失败
                    return Future.failedFuture(err);
                }
            });
    }
}