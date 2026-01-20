package io.zerows.plugins.oauth2.metadata;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Arrays;

/**
 * OAuth2 授权模式枚举
 * <p>
 * 严格对应 Spring Security OAuth2 的 {@code AuthorizationGrantType} 定义。
 * 已移除不安全的 Password 和 Implicit 模式。
 */
public enum OAuth2GrantType {

    /**
     * 授权码模式 (RFC 6749)
     * value: authorization_code
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * 刷新令牌 (RFC 6749)
     * value: refresh_token
     */
    REFRESH_TOKEN("refresh_token"),

    /**
     * 客户端凭证模式 (RFC 6749)
     * value: client_credentials
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * JWT 授权模式 (RFC 7523)
     * value: urn:ietf:params:oauth:grant-type:jwt-bearer
     */
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),

    /**
     * 设备编码模式 (RFC 8628)
     * value: urn:ietf:params:oauth:grant-type:device_code
     */
    DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code"),

    /**
     * 令牌交换模式 (RFC 8693)
     * value: urn:ietf:params:oauth:grant-type:token-exchange
     */
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange");

    @Getter
    private final String value;

    OAuth2GrantType(final String value) {
        this.value = value;
    }

    /**
     * 根据字符串查找枚举
     *
     * @param value 请求参数中的 grant_type
     * @return OAuth2GrantType 或 null
     */
    public static OAuth2GrantType from(final String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(type -> type.value.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }

    /**
     * 校验传入的字符串是否匹配当前模式 (忽略大小写)
     *
     * @param input 请求参数中的 grant_type
     * @return true / false
     */
    public boolean match(final String input) {
        return StrUtil.equalsIgnoreCase(this.value, input);
    }
}