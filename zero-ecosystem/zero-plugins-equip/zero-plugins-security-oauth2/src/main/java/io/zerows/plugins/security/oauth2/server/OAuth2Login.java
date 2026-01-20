package io.zerows.plugins.security.oauth2.server;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Redirect;
import io.zerows.plugins.security.service.AsyncUserAt;
import io.zerows.plugins.security.service.BasicLoginRequest;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@EndPoint
@Slf4j
public class OAuth2Login {

    @POST
    @Path("/login")
    @Redirect // 👈 告诉框架：这个方法返回的 String 就是跳转的目标地址 (Location)
    public Future<String> handleLogin(
        @FormParam("username") final String username,
        @FormParam("password") final String password,
        @FormParam("return_to") final String returnTo,
        final Session session
    ) {
        // 1. 构建请求
        final BasicLoginRequest request = new BasicLoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        // 2. 调用 Service
        final AsyncUserAt userService = AsyncUserAt.of(request.type());

        // 3. 返回 Future<String>
        return userService.loadLogged(request).compose(userAt -> {
            // ---------------------------------------------------------
            // ✅ A. 登录成功
            // ---------------------------------------------------------
            log.info("[ ZERO ] 用户登录成功: {}", username);

            // 3.1 写入 Session (HTTP 层面操作)
            if (session != null) {
                session.put("user", userAt.id().toString());
                // 防止会话固化
                session.regenerateId();
            }

            // 3.2 计算成功跳转地址
            String location = "/";
            if (StrUtil.isNotBlank(returnTo)) {
                location = returnTo;
            }

            // 返回地址，框架会自动执行 302 跳转
            return Future.succeededFuture(location);
        }).recover(ex -> {
            // ---------------------------------------------------------
            // ❌ B. 登录失败
            // ---------------------------------------------------------
            log.warn("[ ZERO ] 用户登录失败: {}", ex.getMessage());

            // 4.1 计算失败跳转地址
            final StringBuilder sb = new StringBuilder("/login.html?error=true");
            if (StrUtil.isNotBlank(returnTo)) {
                sb.append("&return_to=")
                    .append(URLEncoder.encode(returnTo, StandardCharsets.UTF_8));
            }

            // 4.2 返回地址
            // 注意：必须使用 succeededFuture，因为我们要执行正常的 302 跳转，而不是抛出 500 错误
            return Future.succeededFuture(sb.toString());
        });
    }
}