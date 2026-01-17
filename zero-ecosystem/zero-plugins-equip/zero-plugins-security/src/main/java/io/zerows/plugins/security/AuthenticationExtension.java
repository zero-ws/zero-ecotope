package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;

/**
 * 扩展认证模块的接口，一次性加载所有合法的 SPI 等待使用
 */
public interface AuthenticationExtension {

    boolean support(String authorization);

    AuthenticationHandler handlerOf(Vertx vertx, AuthenticationProvider provider, JsonObject options);
}
