package io.zerows.plugins.oauth2.client;

import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2ConfigClient;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * OAuth2 客户端构建器
 * <p>
 * 负责将 YAML 配置模型 {@link OAuth2ConfigClient}
 * 转换为 数据库实体模型 {@link Oauth2RegisteredClient}
 * </p>
 *
 * @author lang
 */
public class BuilderOfOAuth2Client extends AbstractBuilder<Oauth2RegisteredClient> {

    @Override
    public <R> Oauth2RegisteredClient create(final R source) {
        // 只接受 OAuth2ConfigClient 类型的源数据
        if (source instanceof final OAuth2ConfigClient config) {
            final Oauth2RegisteredClient client = new Oauth2RegisteredClient();

            // 1. 基础标识信息映射
            // 如果配置中未指定 ID，则生成随机 UUID (通常 YAML 配置不带 ID)
            client.setId(config.getId() == null ? UUID.randomUUID().toString() : config.getId());
            client.setClientId(config.getClientId());

            // 签发时间：优先用配置的，没有则取当前时间
            client.setClientIdIssuedAt(config.getClientIdIssuedAt() == null ?
                LocalDateTime.now() : config.getClientIdIssuedAt());

            client.setClientSecret(config.getClientSecret());
            client.setClientSecretExpiresAt(config.getClientSecretExpiresAt());
            client.setClientName(config.getClientName());

            // 2. 租户与应用标识
            client.setTenantId(config.getTenantId());
            client.setAppId(config.getAppId());

            // 3. 集合类型转字符串 (逗号分隔)
            // Set<String> -> "val1,val2,val3"
            client.setClientAuthenticationMethods(this.join(config.getClientAuthenticationMethods()));
            client.setAuthorizationGrantTypes(this.join(config.getAuthorizationGrantTypes()));
            client.setRedirectUris(this.join(config.getRedirectUris()));
            client.setPostLogoutRedirectUris(this.join(config.getPostLogoutRedirectUris()));
            client.setScopes(this.join(config.getScopes()));

            // 4. 复杂对象转 JSON 字符串
            // ClientSetting
            if (config.getClientSettings() != null) {
                client.setClientSettings(config.getClientSettings().toJson().encode());
            }

            // TokenSetting
            if (config.getTokenSettings() != null) {
                client.setTokenSettings(config.getTokenSettings().toJson().encode());
            }

            // Ext (JsonObject)
            if (config.getExt() != null) {
                client.setExt(config.getExt().encode());
            }

            return client;
        }
        return null;
    }

    /**
     * 辅助方法：将 Set<String> 拼接为逗号分隔字符串
     *
     * @param values 字符串集合
     * @return 逗号分隔的字符串，如果集合为空则返回 null
     */
    private String join(final Set<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(",", values);
    }
}