package io.zerows.plugins.security.oauth2.server.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.metadata.OAuth2Credential;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class OAuthTool {
    private static final String BASIC_PREFIX = "Basic ";

    public static OAuth2Credential fromHeader(final String authorization) {
        if (StrUtil.isBlank(authorization) || !authorization.startsWith(BASIC_PREFIX)) {
            return null;
        }

        try {
            // 1. 截取 Base64 部分 (忽略 "Basic " 后的空格)
            final String base64Token = authorization.substring(BASIC_PREFIX.length()).trim();

            // 2. Base64 解码 (RFC 7617 规定使用 UTF-8)
            final String decoded = Base64.decodeStr(base64Token, StandardCharsets.UTF_8);

            // 3. 按照第一个冒号 ":" 拆分 ID 和 Secret
            if (StrUtil.contains(decoded, ":")) {
                // limit = 2 是为了防止 client_secret 中包含冒号导致被误切
                final String[] parts = decoded.split(":", 2);
                return new OAuth2Credential(parts[0], parts[1]).isBasic(true);
            } else {
                // 只有 client_id 的特殊情况 (Public Client)
                return new OAuth2Credential(decoded, StrUtil.EMPTY).isBasic(true);
            }
        } catch (final Exception ex) {
            log.warn("{} 认证 Header 解析异常: {}", OAuth2Constant.K_PREFIX, ex.getMessage());
            return null;
        }
    }
}
