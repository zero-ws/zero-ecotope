package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.VertxException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.handler.HttpFilter;
import io.zerows.plugins.oauth2.OAuth2Constant;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 授权端点拦截器 (纯 Session 检查版)
 */
@Slf4j
@WebFilter("/oauth2/authorize")
public class AuthFilter extends HttpFilter {

    private static final String LOGIN_PAGE = "/login.html";
    private static final String SAVED_REQUEST_KEY = "ZERO_SAVED_REQUEST";

    @Override
    public void doGet(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
        final RoutingContext context = this.getContext();
        // ---------------------------------------------------------------------
        // 核心逻辑：只检查 Session 中是否有用户 ID
        // ---------------------------------------------------------------------
        boolean isLogged = false;

        // 1. 依然保留标准检查 (兼容性)
        if (context.user() != null) {
            isLogged = true;
        }
        // 2. ✅ 核心：直接检查 Session Key
        // 不再尝试构造 User 对象，不再触碰 setUser 等内部 API
        else if (context.session() != null && context.session().get(OAuth2Constant.K_SESSION) != null) {
            isLogged = true;
            // 此时我们只是简单的 flag = true，不修改 context 内部状态
        }

        // ---------------------------------------------------------------------
        // 3. 结果判定
        // ---------------------------------------------------------------------
        if (isLogged) {
            // ✅ 已登录：直接放行
            // 注意：后续的 Service/Agent 里，不能调用 context.user()，必须调用 context.session().get(...)
            // this.doFilter(request, response);
            return;
        }

        // ---------------------------------------------------------------------
        // 4. 未登录：拦截跳转 (保持不变)
        // ---------------------------------------------------------------------
        final String originalUrl = request.absoluteURI();
        log.info("{} 用户未登录，拦截授权请求: {}", OAuth2Constant.K_PREFIX, originalUrl);

        if (context.session() != null) {
            context.session().put(SAVED_REQUEST_KEY, originalUrl);
        } else {
            log.warn("{} Session 未启用，无法保存跳转前的请求！", OAuth2Constant.K_PREFIX);
        }

        final String encodedUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
        final String redirectLocation = LOGIN_PAGE + "?return_to=" + encodedUrl;

        if (!response.ended()) {
            response.setStatusCode(302);
            response.putHeader(HttpHeaders.LOCATION, redirectLocation);
            response.end();
        }
    }
}