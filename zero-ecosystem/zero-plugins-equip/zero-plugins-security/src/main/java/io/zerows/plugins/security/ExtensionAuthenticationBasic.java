package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.impl.Utils;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * <pre>
 * 🟢 HTTP Basic 认证扩展实现
 *
 * 1. 🌐 全局说明
 *    实现了标准 HTTP Basic Authentication 协议的解析逻辑。
 *    作为 `AuthenticationHandlerGateway` 的默认兜底策略之一。
 *
 * 2. 🎯 核心逻辑
 *    - 支持检测：匹配 "Basic " 前缀的 Authorization 头。
 *    - 解析逻辑：
 *      1. 提取 Base64 编码部分。
 *      2. 解码并按 ":" 分割为 username 和 password。
 *      3. 封装为 `UsernamePasswordCredentials`。
 *
 * 3. 🔄 结果模式
 *    返回 "待验证" 状态 (Credentials) 的 `ExtensionAuthenticationResult`。
 *    真实的密码校验工作后续由 `AuthenticationProvider` 完成。
 * </pre>
 */
@Slf4j
public class ExtensionAuthenticationBasic implements ExtensionAuthentication {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("Basic 权限认证失败，提供有效令牌！");

    /**
     * <pre>
     * 🟢 绑定安全墙类型
     *
     * 对应 defaults 配置中的安全墙。通常 Basic 认证属于基础墙的一部分。
     * </pre>
     *
     * @return {@link SecurityConstant#WALL_BASIC}
     */
    @Override
    public String name() {
        return SecurityConstant.WALL_BASIC;
    }

    /**
     * <pre>
     * 🟢 策略支持判断
     *
     * - 检测 Authorization 头是否非空。
     * - 检测 Schema 是否为 "Basic"。
     * </pre>
     *
     * @param authorization HTTP Authorization Header
     * @return true if schema is Basic
     */
    @Override
    public boolean support(final String authorization) {
        final TokenType token = TokenType.fromString(authorization);
        return TokenType.BASIC == token;
    }

    /**
     * <pre>
     * 🟢 执行 Basic 解析流程
     *
     * 1. 🧶 解码
     *    - 移除 "Basic " 前缀。
     *    - 对剩余字符串进行 Base64 解码。
     *
     * 2. ✂️ 分割
     *    - 将解码后的字符串按第一个 ":" 分割。
     *    - 前半部分为 Username，后半部分为 Password。
     *
     * 3. 📦 封装
     *    - 构造 `UsernamePasswordCredentials` 对象。
     *    - 返回 `ExtensionAuthenticationResult.bindAsync(credentials)`。
     *    - 仅做格式解析，不做密码校验。校验交由后续 AuthProvider。
     * </pre>
     *
     * @param input Authorization Header Container
     * @param vertx Vert.x Instance
     * @param meta  Security Meta
     * @return Async Result with Credentials
     */
    @Override
    public Future<ExtensionAuthenticationResult> resolve(final JsonObject input, final Vertx vertx, final SecurityMeta meta) {
        // Authorization 请求头提取
        final String authorization = Ut.valueString(input, HttpHeaders.AUTHORIZATION.toString());
        // Basic 缺失 Provider
        final String suser;
        final String spass;

        try {
            // decode the payload
            final int idx = authorization.indexOf(' ');
            final String header = authorization.substring(idx + 1);
            final String decoded = new String(Utils.base64Decode(header), StandardCharsets.UTF_8);

            final int colonIdx = decoded.indexOf(":");
            if (colonIdx != -1) {
                suser = decoded.substring(0, colonIdx);
                spass = decoded.substring(colonIdx + 1);
            } else {
                suser = decoded;
                spass = null;
            }
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(suser, spass);
            return Future.succeededFuture(ExtensionAuthenticationResult.bindAsync(credentials));
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            return Future.failedFuture(UNAUTHORIZED);
        }
    }
}
