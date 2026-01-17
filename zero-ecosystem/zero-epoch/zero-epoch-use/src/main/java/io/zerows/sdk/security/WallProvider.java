package io.zerows.sdk.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.metadata.security.SecurityMeta;

import java.util.Set;

/**
 * 此处的 Provider 会形成链式结构
 * <pre>
 *     1. 基础内置的 Provider，依靠 {@link Wall} 中的定义进行区分，进行首次认证
 *     2. 用户自定义的 Provider，会排在后边进行二次认证，二次认证就要访问数据库等外部资源
 *        启用 {@link WallExecutor} 内置的方法进行调用
 * </pre>
 * 内置的 Provider 可替换，根据载入的不同组件进行替换操作形成完整的插件结构，除了 {@link BasicAuthHandler} 以外，其他
 * 配置都依赖插件接入过程，实现完整插件模式的引入！
 *
 * @author lang : 2025-10-29
 */
public interface WallProvider {

    AuthenticationProvider providerOfAuthentication(Vertx vertxRef, Set<SecurityMeta> meta);

    AuthenticationHandler handlerOfAuthentication(Vertx vertxRef, Set<SecurityMeta> metaSet);

    AuthorizationHandler handlerOfAuthorization(Vertx vertxRef, Set<SecurityMeta> metaSet);
}
