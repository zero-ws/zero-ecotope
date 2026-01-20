package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.VertxException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.handler.HttpFilter;
import io.zerows.plugins.oauth2.OAuth2Constant;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 授权端点拦截器
 * 职责：
 * 1. 拦截 /oauth2/authorize 请求
 * 2. 检查用户是否已登录 (Session)
 * 3. 未登录 -> 保存当前 URL -> 302 重定向到登录页
 * 4. 已登录 -> 放行 (让后续的 AuthService 处理生成 Code)
 */
@Slf4j
@WebFilter("/oauth2/authorize")
public class AuthFilter extends HttpFilter {

    // 假设你的登录页地址 (如果是前后端分离，可能是前端路由；如果是服务端渲染，可能是静态页)
    private static final String LOGIN_PAGE = "/login.html"; // 或者 /login-view
    private static final String SAVED_REQUEST_KEY = "ZERO_SAVED_REQUEST";

    @Override
    public void doGet(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
        final RoutingContext context = this.getContext();

        // 1. 检查用户是否已登录
        // ZeroWS/Vert.x Web 标准获取 User 的方式
        final User user = context.user();

        if (user != null) {
            // ✅ 已登录：直接放行，进入后续 Worker/Service 逻辑
            this.doFilter(request, response);
            return;
        }

        // 🛑 2. 未登录：执行拦截跳转逻辑

        // A. 获取当前完整的请求 URL (包含 client_id, redirect_uri 等查询参数)
        final String originalUrl = request.absoluteURI();
        log.info("{} 用户未登录，拦截授权请求: {}", OAuth2Constant.K_PREFIX, originalUrl);

        // B. 将原始 URL 保存到 Session 中
        // 这样登录成功后，Login 接口可以读出这个 URL 并跳回来
        if (context.session() != null) {
            context.session().put(SAVED_REQUEST_KEY, originalUrl);
        } else {
            log.warn("{} Session 未启用，无法保存跳转前的请求！", OAuth2Constant.K_PREFIX);
        }

        // C. 构造登录跳转地址
        // 通常带上 return_to 参数给前端，或者完全依赖 Session
        final String encodedUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
        final String redirectLocation = LOGIN_PAGE + "?return_to=" + encodedUrl;

        // D. 执行 302 重定向
        response.setStatusCode(302);
        response.putHeader(HttpHeaders.LOCATION, redirectLocation);
        response.end(); // 结束响应，不再走后续逻辑
    }
}