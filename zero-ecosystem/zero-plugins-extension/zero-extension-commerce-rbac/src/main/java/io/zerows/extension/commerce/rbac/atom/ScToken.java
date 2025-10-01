package io.zerows.extension.commerce.rbac.atom;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.corpus.metadata.MultiKeyMap;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;
import io.zerows.specification.atomic.HJson;

import java.io.Serializable;
import java.util.Objects;

/**
 * 新版的 Token 放到一个带有计时器的缓存中，此缓存用来保存 JWT 的标准结构，此处的 JWT 版本为非标准结构
 * <pre><code>
 *     Token
 *     {
 *         "access_token": "???",
 *         "refresh_token": "???"
 *         "exp": "(超时）秒",
 *         "iat": "签发时间"
 *     }
 * </code></pre>
 * 缓存中的数据对应的数据结构
 * <pre><code>
 *     1. {@link ScClock}
 *        userKey = {@link MultiKeyMap}
 *                  userKey = ScToken
 *                  token = ScToken
 *        ( vector ), refresh_token = token 缓存中直接存
 *     2. {@link ScClock} 带有 TTL 设置，使用了 SharedMap 来存储，时间到了之后会过期
 *     3. ScToken 多出来的两个属性
 *        - accessBytes / refreshBytes
 *        二进制格式的 token（比对专用）
 * </code></pre>
 *
 * @author lang : 2024-09-14
 */
public class ScToken implements Serializable {

    private final String clientId;
    private byte[] authToken;
    private byte[] authRefresh;
    private long exp;
    private long iat;

    private ScToken(final String clientId) {
        this.clientId = clientId;
    }

    public static ScToken of(final String clientId) {
        Objects.requireNonNull(clientId);
        return new ScToken(clientId);
    }

    public String id() {
        return this.clientId;
    }

    public ScToken duration(final long iat, final long exp) {
        this.iat = iat;
        this.exp = exp;
        return this;
    }

    public long issuedAt() {
        return this.iat;
    }

    public long expiredAt() {
        return this.exp;
    }

    public ScToken token(final String token) {
        this.authToken = token.getBytes(VValue.DFT.CHARSET);
        return this;
    }

    public String token() {
        return new String(this.authToken, VValue.DFT.CHARSET);
    }

    public ScToken refreshToken(final String refresh) {
        this.authRefresh = refresh.getBytes(VValue.DFT.CHARSET);
        return this;
    }

    public String refreshToken() {
        return new String(this.authRefresh, VValue.DFT.CHARSET);
    }

    public byte[] authToken() {
        return this.authToken;
    }

    public byte[] authRefresh() {
        return this.authRefresh;
    }

    /**
     * 生成 Token 对应的基本数据序列化结果
     * <pre><code>
     *     {
     *         "access_token": "??",
     *         "refresh_token": "??",
     *         "exp": ???,
     *         "iat": ???
     *     }
     * </code></pre>
     * 早期的版本实现了 {@link HJson} 接口，但最新版本不使用此接口，而是直接使用特殊方法来完成响应数据的构造，响应构造过程中不包含 id
     * 属性，和序列化以及反序列化有很大的区别。
     *
     * @return 返回Json数据结构
     */
    public JsonObject authResponse() {
        final JsonObject tokenJ = new JsonObject();
        tokenJ.put(KName.ACCESS_TOKEN, this.token());
        tokenJ.put("refresh_token", this.refreshToken());
        tokenJ.put("iat", this.iat);
        tokenJ.put("exp", this.exp);
        return tokenJ;
    }
}
