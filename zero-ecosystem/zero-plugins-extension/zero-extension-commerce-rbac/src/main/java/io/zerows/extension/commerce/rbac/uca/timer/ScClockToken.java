package io.zerows.extension.commerce.rbac.uca.timer;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VValue;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.atom.ScToken;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.extension.commerce.rbac.exception._80206Exception401TokenCounter;
import io.zerows.extension.commerce.rbac.exception._80207Exception401TokenInvalid;
import io.zerows.extension.commerce.rbac.exception._80208Exception401TokenExpired;
import io.zerows.unity.Ux;
import org.osgi.framework.Bundle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 带计时器的数据池，用于保存令牌相关信息
 * <pre><code>
 *     1. 令牌的超时时间默认 120 min -> 2 小时
 *     2. 令牌直接使用 JWT 方式生成，后期追加算法调整
 *     3. 缓存池的名称 POOL_TOKEN
 *     4. 键：userId （登录之后，所以使用用户ID）
 * </code></pre>
 *
 * @author lang : 2024-09-15
 */
class ScClockToken extends AbstractClock<ScToken> {
    private static final ScConfig CONFIG = ScPin.getConfig();
    private static final ConcurrentMap<String, String> POINTER = new ConcurrentHashMap<>();

    ScClockToken(final Bundle bundle) {
        super(bundle, ScConstant.POOL_TOKEN);
    }

    @Override
    public int configTtl() {
        return CONFIG.getTokenExpired();
    }


    /**
     * 此处的 data 的数据结构
     * <pre><code>
     *     {
     *         "user": "X_USER 对应的主键 KEY 值，用户 ID",
     *         "session": "Vert.x 生成的 Session ID",
     *         "habitus": "根据当前用户和 session 生成的 128 bit 的随机码，保证唯一性和标识性",
     *         "role": [
     *             "当前用户关联的角色信息"
     *         ],
     *         "group": [
     *             "当前用户关联的用户组信息"
     *         ]
     *     }
     * </code></pre>
     * 此处生成的 ScToken 产生的响应信息
     * <pre><code>
     *     {
     *         "access_token": "??",
     *         "refresh_token": "??",
     *         "exp": "??",
     *         "iat": "??"
     *     }
     * </code></pre>
     *
     * @param data 基础内容数据
     *
     * @return 生成好的令牌
     */
    @Override
    public ScToken generate(final JsonObject data) {
        final String userId = Ut.valueString(data, KName.USER);
        /*
         * WebToken 数据抽取，移除 role, group 两个数据，生成的 token 中的数据只包含
         * {
         *     "user": "???",
         *     "session": "???",
         *     "habitus": "128 bit"
         * }
         */
        final JsonObject tokenData = data.copy();
        tokenData.remove(KName.ROLE);
        tokenData.remove(KName.GROUP);


        /*
         * 生成 access_token 的值，其中
         * iat: 当前时间
         * exp: 过期时间
         * refresh_token: 重新生成刷新令牌（包含数据）
         * {
         *     "id": "???",
         *     "access_token": "???"
         * }
         */
        final ScToken token = ScToken.of(userId);
        final long iat = new Date().getTime();
        final long exp = iat + TimeUnit.SECONDS.toMillis(CONFIG.getTokenExpired());
        token.duration(iat, exp);

        /*
         * 构造 ScToken 对象（双Token）
         * - token
         * - refreshToken
         */
        final String vToken = Ux.Jwt.token(tokenData);
        final JsonObject refreshData = new JsonObject();
        refreshData.put(KName.ID, userId);
        refreshData.put(KName.ACCESS_TOKEN, vToken);
        final String vRefresh = Ux.Jwt.token(refreshData);
        return token.token(vToken).refreshToken(vRefresh);
    }

    @Override
    public Future<ScToken> get(final String key, final boolean isOnce) {
        return super.get(key, isOnce).compose(scToken -> {
            if (isOnce) {
                final String refreshToken = scToken.refreshToken();
                POINTER.remove(refreshToken);
                this.logger().info("Once token will remove `refreshToken` = {} related records.", refreshToken);
            }
            return Ux.future(scToken);
        });
    }

    @Override
    public Future<ScToken> put(final String key, final ScToken value, final String... moreKeys) {
        return super.put(key, value, moreKeys).compose(scToken -> {
            final Set<String> keySet = new HashSet<>();
            keySet.add(key);
            keySet.addAll(Arrays.stream(moreKeys)
                .filter(refreshKey -> !refreshKey.equals(scToken.refreshToken()))
                .collect(Collectors.toSet())
            );
            // refreshToken = relatedKeys
            keySet.forEach(related -> POINTER.put(scToken.refreshToken(), related));
            this.logger().info("The refresh token `{}` will get {} relations.",
                scToken.refreshToken(), keySet.size());
            return Ux.future(scToken);
        });
    }

    /**
     * 异常说明
     * <pre><code>
     *     {@link _80206Exception401TokenCounter} 无法找到 Token
     *     {@link _80207Exception401TokenInvalid} Token 不匹配
     *     {@link _80208Exception401TokenExpired} Token 超时
     * </code></pre>
     *
     * @param stored   缓存中存储的值
     * @param waiting  等待验证的值（字面量）
     * @param identity 验证标识
     *
     * @return 验证结果（异步）
     */
    @Override
    @SuppressWarnings("unchecked")
    public Future<Boolean> verify(final ScToken stored, final String waiting, final String identity) {
        // 无法找到 Token
        if (Objects.isNull(stored)) {
            // WebToken size
            this.logger().info(AuthMsg.TOKEN_SIZE_NULL, null, identity);
            return FnVertx.failOut(_80206Exception401TokenCounter.class, 0, identity);
        }


        // Token 不匹配
        final byte[] authBytes = waiting.getBytes(VValue.DFT.CHARSET);
        if (!Arrays.equals(authBytes, stored.authToken())) {
            // WebToken invalid
            this.logger().info(AuthMsg.TOKEN_INVALID, waiting);
            return FnVertx.failOut(_80207Exception401TokenInvalid.class, waiting);
        }


        // Token 超时
        final long currentAt = new Date().getTime();
        final long expiredAt = stored.expiredAt();
        if (expiredAt < currentAt) {
            this.logger().info(AuthMsg.TOKEN_EXPIRED, waiting, expiredAt);
            return FnVertx.failOut(_80208Exception401TokenExpired.class, waiting);
        }
        return Ux.futureT();
    }

    /**
     * 这个方法对 Token 而言要执行重算
     * <pre><code>
     *     输入可能包含
     *     1. userId
     *     2. refreshToken
     *     3. accessToken
     * </code></pre>
     * {@link ScClockToken#POINTER} 中会包含
     * <pre><code>
     *     refreshToken = [userId, accessToken]
     * </code></pre>
     * 第一输入是 token
     *
     * @param keys 所有键值
     *
     * @return 异步删除结果
     */
    @Override
    public Future<Boolean> remove(final String... keys) {
        final Set<String> rmSuper = new HashSet<>();
        final Set<String> rmChild = new HashSet<>();
        Arrays.stream(keys).filter(Objects::nonNull).forEach(keyInput -> POINTER.forEach((key, values) -> {
            if (keyInput.equals(key) || values.equals(keyInput)) {
                // 删除父类专用
                rmSuper.add(key);
                rmSuper.add(values);


                // 删除 POINTER 专用
                rmChild.add(key);
            }
        }));
        return super.remove(rmSuper.toArray(new String[]{})).compose(removed -> {
            if (removed) {
                rmChild.forEach(POINTER::remove);
            }
            return Ux.futureF();
        });
    }
}
