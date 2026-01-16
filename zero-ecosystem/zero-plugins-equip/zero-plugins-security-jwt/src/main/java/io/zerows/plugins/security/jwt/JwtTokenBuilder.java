package io.zerows.plugins.security.jwt;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Vert.x (ZeroWS) 版 JWT Token 构建器
 * 负责 Access Token 的生成/解析 (基于 LeeJwt)
 * 负责 Refresh Token 的生命周期管理 (基于 UserCache)
 *
 * @author lang
 */
@Slf4j
public class JwtTokenBuilder extends TokenBuilderBase {
    // 标准 Claim
    private static final String NAME_SUBJECT = "subject";                   // sub
    private static final String NAME_ISSUER = "issuer";                     // iss
    private static final String NAME_AUDIENCE = "audience";                 // aud
    private static final String NAME_EXPIRE_SEC = "expiresInSeconds";       // exp
    private static final String NAME_IAT = "noTimestamp";                   // iat
    private static final String NAME_LEEWAY = "leeway";                     // leeway
    // 自定义扩展字段
    private static final String NAME_ADDON_DATA = "ext";
    /**
     * 配置项
     */
    private static final String KEY_CFG_ISSUER = "issuer";                  // issuer           , iss
    private static final String KEY_CFG_AUDIENCE = "audience";              // audience         , aud
    private static final String KEY_CFG_EXPIRED_AT = "expiredAt";           // expiredAt        , exp
    private static final String KEY_CFG_IAT = "iat";                        // iat              , iat
    private static final String KEY_CFG_LEEWAY = "leeway";                  // leeway           , leeway
    // 核心编解码器引用
    private final JwtToken codec;

    public JwtTokenBuilder() {
        // 初始化编解码器，LeeJwt 内部会自动读取 vertx.yaml 中的 security 配置
        this.codec = JwtToken.of();
    }

    /**
     * 解析 Access Token 获取用户 ID (Subject)
     *
     * @param token JWT 字符串
     * @return 用户 ID (sub)，如果无效或解析失败返回 null
     */
    @Override
    public String accessOf(final String token) {
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return null;
        }
        if (!this.tokenValidate(token)) {
            return null;
        }
        try {
            // 使用 LeeJwt 解码，如果验证失败内部通常会返回空 JsonObject 或抛出异常
            final JsonObject payload = this.codec.decode(token);

            if (payload == null || payload.isEmpty()) {
                return null;
            }
            // 检查过期时间 (Vert.x decode 方法通常已验证 exp，此处为双重保险或读取业务字段)
            // 提取 standard claim: sub
            return payload.getString(NAME_SUBJECT);
        } catch (final Exception e) {
            // 解码失败（签名错误、过期等）
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 为用户生成 Access Token
     *
     * @param userAt 用户会话对象
     * @return JWT 字符串
     */
    @Override
    public String accessOf(final UserAt userAt) {
        // 1. 确保用户已授权/数据完整 (调用父类逻辑)
        final MSUser logged = this.ensureAuthorized(userAt);
        // 2. 构造 Token Payload
        final JsonObject payload = this.tokenGenerate(userAt.id().toString(), logged.token());
        // 3. 编码生成 Token
        return this.codec.encode(payload);
    }

    /**
     * 生成并存储 Refresh Token
     *
     * @param userAt 用户会话对象
     * @return Refresh Token 字符串 (UUID)
     */
    @Override
    public String refreshOf(final UserAt userAt) {
        this.ensureAuthorized(userAt);
        // 提取配置信息
        final SecurityConfig config = SecurityActor.configJwt();
        if (Objects.isNull(config)) {
            return null;
        }
        // 1. 生成一个安全的随机 Refresh Token
        final String refreshToken = UUID.randomUUID().toString().replace("-", ""); // 移除 UUID 中的横线

        // 2. 获取 UserCache 实例
        final UserCache userCache = UserCache.of();

        // 3. 将 Refresh Token 与用户 ID 存储到缓存
        userCache.tokenRefresh(refreshToken, userAt.id()); // 假设 userId 是 UUID 字符串

        return refreshToken;
    }

    private boolean tokenValidate(final String token) {
        // 提取配置信息
        final SecurityConfig config = SecurityActor.configJwt();
        if (Objects.isNull(config)) {
            return false;
        }
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            final JsonObject payload = this.codec.decode(token);
            if (payload == null || payload.isEmpty()) {
                return false;
            }
            // 额外校验 issuer / audience（如配置）
            final String issuer = config.option(KEY_CFG_ISSUER, null);
            if (StrUtil.isNotEmpty(issuer)) {
                final String tokenIssuer = payload.getString(NAME_ISSUER);
                if (!issuer.equals(tokenIssuer)) {
                    return false;
                }
            }

            final String audience = config.option(KEY_CFG_AUDIENCE, null);
            if (StrUtil.isNotEmpty(audience)) {
                final String tokenAudience = payload.getString(NAME_AUDIENCE);
                return Objects.equals(audience, tokenAudience);
            }
            return true;
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    private JsonObject tokenGenerate(final String identifier, final Map<String, Object> data) {
        // 提取配置信息
        final SecurityConfig config = SecurityActor.configJwt();
        if (Objects.isNull(config)) {
            return null;
        }
        final String expires = config.option(KEY_CFG_EXPIRED_AT, "2h");
        final Duration duration = R2MO.toDuration(expires);

        final long nowMs = System.currentTimeMillis();
        final long expS = Duration.ofMillis(nowMs + duration.toMillis()).toSeconds();


        final JsonObject tokenData = new JsonObject();
        // habitus / Fix: 用户会话标识缺失，无法获取缓存！
        tokenData.put(KName.HABITUS, identifier);
        // id      / Fix: 可调用 Account.userId(User user) 获取用户 ID
        tokenData.put(KName.ID, identifier);

        // subject
        tokenData.put(NAME_SUBJECT, identifier);
        final String issuer = config.option(KEY_CFG_ISSUER, null);
        if (StrUtil.isNotEmpty(issuer)) {
            // issuer
            tokenData.put(NAME_ISSUER, issuer);
        }
        // expiresInSeconds
        tokenData.put(NAME_EXPIRE_SEC, expS);
        final Object audience = config.option(KEY_CFG_AUDIENCE, null);
        final JsonArray audienceArr = new JsonArray();
        if (audience instanceof final String audienceStr) {
            Arrays.stream(audienceStr.split(",")).forEach(audienceArr::add);
        } else if (audience instanceof final JsonArray audienceA) {
            audienceArr.addAll(audienceA);
        }
        // audience
        tokenData.put(NAME_AUDIENCE, audienceArr);


        // noTimestamp
        final boolean iat = config.option(KEY_CFG_IAT, Boolean.FALSE);
        tokenData.put(NAME_IAT, iat);
        // leeway
        final long leeway = config.option(KEY_CFG_LEEWAY, 0L);
        tokenData.put(NAME_LEEWAY, leeway);

        final JsonObject additionalData = Ut.toJObject(data);
        if (Ut.isNotNil(additionalData)) {
            tokenData.put(NAME_ADDON_DATA, additionalData);
        }
        return tokenData;
    }
}