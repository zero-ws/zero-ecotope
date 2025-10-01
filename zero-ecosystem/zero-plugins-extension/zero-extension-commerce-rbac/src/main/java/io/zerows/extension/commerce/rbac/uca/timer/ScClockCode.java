package io.zerows.extension.commerce.rbac.uca.timer;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.extension.commerce.rbac.exception._80200Exception401CodeWrong;
import io.zerows.extension.commerce.rbac.exception._80201Exception401CodeExpired;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * 带计时器的数据池，用于保存授权码相关信息
 * <pre><code>
 *     1. 授权码的超时时间在 {@link ScConfig#getCodeExpired()} 可配置，默认 30s
 *     2. 授权码的长度在 {@link ScConfig#getCodeLength()} 配置，默认 4
 *     3. 缓存池的名称 POOL_CODE
 *     4. 键：sessionId
 * </code></pre>
 *
 * @author lang : 2024-09-14
 */
class ScClockCode extends AbstractClock<String> {
    private static final ScConfig CONFIG = ScPin.getConfig();

    ScClockCode(final Bundle bundle) {
        super(bundle, ScConstant.POOL_CODE);
    }

    @Override
    public String generate() {
        final int length = CONFIG.getCodeLength();
        final String code = Ut.randomString(length);
        this.logger().info("Generated Authorization Code: {}", code);
        return code;
    }

    @Override
    public String generate(final JsonObject config) {
        throw new _60050Exception501NotSupport(this.getClass());
    }


    @Override
    public int configTtl() {
        return CONFIG.getCodeExpired();
    }

    /**
     * 异常说明
     * <pre><code>
     *     {@link _80201Exception401CodeExpired} 授权码过期异常
     *     {@link _80200Exception401CodeWrong} 授权码错误异常
     * </code></pre>
     *
     * @param stored   缓存中存储的值
     * @param waiting  等待验证的值（字面量）
     * @param identity 验证标识
     *
     * @return 验证结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public Future<String> verify(final String stored, final String waiting, final String identity) {
        // identity = clientId
        if (Objects.isNull(stored)) {
            // 401: Authorization Code Expired, The item is null, it means that code is expired
            return FnVertx.failOut(_80201Exception401CodeExpired.class, identity, waiting);
        }
        if (!waiting.equals(stored)) {
            // 401: Wrong code provided ( Api Client )
            return FnVertx.failOut(_80200Exception401CodeWrong.class, waiting);
        }
        // Successfully
        return Ux.future(identity);
    }
}
