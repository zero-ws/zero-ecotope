package io.zerows.plugins.security.oauth2;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.zerows.plugins.oauth2.OAuth2Constant;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

/**
 * OAuth2 JWKS (JSON Web Key Set) 生成工具
 * <p>
 * 核心职责：
 * 读取 JKS 文件 -> 提取公钥 -> 使用 Nimbus 转换为标准 JWK JSON -> 包装为 JWKS
 * </p>
 *
 * @author lang
 */
@Slf4j
public class OAuth2Jwks {

    /**
     * 根据 KeyStore 配置生成 JWKS
     *
     * @param options Vert.x 的 KeyStore 配置 (包含路径、密码)
     * @param alias   需要提取的密钥别名 (Key Alias)
     * @return 符合 RFC 7517 标准的 JWKS JsonObject
     */
    public static JsonObject generate(final KeyStoreOptions options, final String alias) {
        // 使用常量前缀进行校验提示
        Objects.requireNonNull(options, OAuth2Constant.K_PREFIX + " KeyStore 配置对象不能为空");
        Objects.requireNonNull(alias, OAuth2Constant.K_PREFIX + " 密钥别名 (alias) 不能为空");

        try {
            // 1. 确定 KeyStore 类型 (默认 JKS)
            final String type = options.getType() != null ? options.getType() : "JKS";
            final KeyStore ks = KeyStore.getInstance(type);

            // 2. 加载 KeyStore 文件流 (从 Classpath)
            final String path = options.getPath();
            if (path == null) {
                throw new IllegalArgumentException(OAuth2Constant.K_PREFIX + " KeyStore 路径不能为空");
            }

            try (final InputStream is = OAuth2Jwks.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    throw new IllegalArgumentException(OAuth2Constant.K_PREFIX + " 无法在 Classpath 下找到 KeyStore 文件: " + path);
                }
                // 加载库文件 (需要 store password)
                ks.load(is, options.getPassword().toCharArray());
            }

            // 3. 提取证书公钥 (Public Key)
            // 注意：提取公钥只需要库密码，不需要 Key 的独立密码
            final Certificate cert = ks.getCertificate(alias);
            if (cert == null) {
                throw new IllegalArgumentException(OAuth2Constant.K_PREFIX + " 无法在 KeyStore [" + path + "] 中找到别名 [" + alias + "] 对应的证书");
            }

            // 目前只支持 RSA 算法 (OAuth2 主流算法)
            if (!(cert.getPublicKey() instanceof RSAPublicKey)) {
                throw new UnsupportedOperationException(OAuth2Constant.K_PREFIX + " 生成 JWKS 失败，目前仅支持 RSA 类型的密钥，当前类型: " + cert.getPublicKey().getAlgorithm());
            }
            final RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

            // 4. 使用 Nimbus JOSE 进行转换 (核心步骤)
            // 自动处理模数(n)和指数(e)的 Base64URL 编码，避免手动计算错误
            final RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)      // 用途: 签名 (sig)
                .algorithm(JWSAlgorithm.RS256) // 算法: RS256 (推荐默认)
                .keyID(alias)                  // kid: 使用别名作为 ID，方便客户端匹配
                .build();

            // 5. 转换为 Vert.x JsonObject
            // Nimbus 输出为 Map<String, Object>，转为 JsonObject
            final JsonObject jwkJson = new JsonObject(rsaKey.toJSONObject());

            // 6. 包装为标准 JWKS 结构: { "keys": [ ... ] }
            final JsonObject jwks = new JsonObject()
                .put("keys", new JsonArray().add(jwkJson));

            log.info("{} JWKS 数据生成成功，密钥别名: {}", OAuth2Constant.K_PREFIX, alias);
            return jwks;

        } catch (final Exception e) {
            log.error("{} JWKS 生成失败，路径: {}, 别名: {}", OAuth2Constant.K_PREFIX, options.getPath(), alias, e);
            throw new RuntimeException(OAuth2Constant.K_PREFIX + " JWKS 生成过程中发生异常", e);
        }
    }
}