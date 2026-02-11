package io.zerows.plugins.security.service;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.program.Ux;

public interface AsyncAuthorization {

    Cc<String, AsyncAuthorization> CC_SKELETON = Cc.openThread();

    static AsyncAuthorization of(final String appId) {
        // Fix: [ XMOD ] 授权组件为空 / resource
        return CC_SKELETON.pick(() -> Ux.waitService(AsyncAuthorization.class), appId);
    }

    static AsyncAuthorization of() {
        return CC_SKELETON.pick(() -> Ux.waitService(AsyncAuthorization.class));
    }

    /**
     * 第一次授权的核心步骤，从 {@link User} 中提取用户信息，构造授权信息，这一步方法会在
     * <pre>
     *     1. 首次登录成功后，若 403 开启 / authorization = true
     *        直接加载 Profile 信息
     *     2. 第二次访问，不再执行 401 认证逻辑，但会包含 habitus 信息
     *        同样加载 Profile 信息
     * </pre>
     * 加载 Profile 的流程
     * <pre>
     *     1. 直接提取 habitus 信息 -> 通过 habitus 获取用户的权限信息
     *        - 若可以获取，直接从缓存拉取
     *        - 若无法获取，则从数据库中加载一次
     *     2. 授权完成！
     * </pre>
     *
     * @param logged 已经登录的用户信息
     * @return 授权之后的用户对象
     */
    Future<User> seekProfile(User logged);

    /**
     * 第二步 /
     * 之前用户已经执行过授权，且更新过信息
     * <pre>
     *     资源访问左值
     * </pre>
     *
     * @param params 授权参数
     * @return 授权结果
     */
    Future<JsonObject> seekResource(JsonObject params);

    /**
     * 第二步 /
     * 核心方法
     * <pre>
     *     资源访问右值
     * </pre>
     *
     * @param user 当前用户信息，包含了之前授权之后的 Profile 信息
     * @return 授权结果，包含了用户的权限信息，格式如下：
     */
    Future<JsonObject> seekAuthorized(User user);
}
