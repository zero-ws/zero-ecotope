package io.zerows.plugins.security.oauth2.server;

import cn.hutool.core.util.StrUtil;
import io.r2mo.openapi.components.schemas.OAuth2LoginRequest;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.Future;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Redirect;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.security.service.AsyncUserAt;
import io.zerows.plugins.security.service.BasicLoginRequest;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@EndPoint
@Slf4j
@Tag(name = DescAuth.group, description = DescAuth.description)
public class OAuth2Login {

    @POST
    @Path("/login")
    @Redirect // 👈 告诉框架：这个方法返回的 String 就是跳转的目标地址 (Location)
    @Operation(
        summary = DescAuth._auth_form_login_summary,
        description = DescAuth._auth_form_login_desc,
        requestBody = @RequestBody(
            description = DescMeta.request_post,
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_FORM_URLENCODED,
                schema = @Schema(implementation = OAuth2LoginRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_302,
                description = DescAuth.OAuth2.return_to,
                content = @Content(
                    mediaType = MediaType.TEXT_HTML,
                    schema = @Schema(type = "string", description = "Location Header URL", example = "/dashboard")
                )
            )
        }
    )
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
            // ---------------------------------------------------------
            // ✅ 核心修复：显式写入 Session
            // ---------------------------------------------------------
            // 只有放入 Session，下一次请求(AuthFilter)才能读到！
            if (session != null) {
                // 存入 ID
                session.put(OAuth2Constant.K_SESSION, userAt.id().toString());

                // ⚠️ 暂时注释 regenerateId 防止版本冲突，等跑通后再开启
                // context.session().regenerateId();
            } else {
                log.error("Session 是空的！无法保存登录状态！");
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