package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;

/**
 * 根据 {@link LoginRequest} 执行不同加载逻辑，加载用户信息（异步版）
 */
public interface AsyncUserAt {
    /**
     * 登录时专用加载用户信息专用接口，使用 Request 交换用户信息（访问数据库）
     *
     * @param request 登录请求
     * @return 用户信息
     */
    Future<UserAt> loadLogged(LoginRequest request);

    /**
     * 登录之后，使用唯一标识符加载用户信息专用接口（访问缓存）
     *
     * @param identifier 唯一标识符（如用户ID、用户名等）
     * @return 用户信息
     */
    Future<UserAt> loadLogged(String identifier);
}
