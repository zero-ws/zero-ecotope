package io.zerows.plugins.oauth2.client;

import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.vertx.ext.auth.KeyStoreOptions;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;

import java.util.Objects;

/**
 * OAuth2 KeyStore 构建器
 * <p>
 * 负责将 Atom 层的配置模型 {@link OAuth2Security.KeyStoreConfig}
 * 转换为 Vert.x Auth 层的 {@link KeyStoreOptions}
 * </p>
 *
 * @author lang
 */
public class BuilderOfOAuth2KeyStore extends AbstractBuilder<KeyStoreOptions> {

    @Override
    public <R> KeyStoreOptions create(final R source) {
        // 只接受 OAuth2Security.KeyStoreConfig 类型的源数据
        if (source instanceof final OAuth2Security.KeyStoreConfig config) {
            final KeyStoreOptions options = new KeyStoreOptions();

            // 1. 基础路径映射 (Path)
            if (Objects.nonNull(config.getPath())) {
                options.setPath(config.getPath());
            }

            // 2. 类型映射 (Type) -> 转大写标准化 (jks -> JKS)
            if (Objects.nonNull(config.getType())) {
                options.setType(config.getType().toUpperCase());
            }

            // 3. 库密码 (Store Password)
            // 设置 keystore 文件本身的访问密码
            if (Objects.nonNull(config.getPassword())) {
                options.setPassword(config.getPassword());
            }

            // 4. [关键] 别名密码保护 (Key Password)
            // 针对 ext.auth.KeyStoreOptions 的特性配置
            // 将 alias 与 password 绑定，确保 JWTAuth 能正确提取指定别名的私钥
            if (Objects.nonNull(config.getAlias()) && Objects.nonNull(config.getPassword())) {
                options.putPasswordProtection(config.getAlias(), config.getPassword());
            }

            return options;
        }
        return null;
    }
}