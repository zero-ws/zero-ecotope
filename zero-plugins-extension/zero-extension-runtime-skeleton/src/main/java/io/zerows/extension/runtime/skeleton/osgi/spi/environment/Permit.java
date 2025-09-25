package io.zerows.extension.runtime.skeleton.osgi.spi.environment;

import io.vertx.core.Future;

/**
 * 许可专用表，用于检查 RBAC 中的所有许可信息
 * <pre><code>
 *     - 验证令牌：WebToken
 *     - 验证身份：401
 *     - 验证权限：403
 * </code></pre>
 *
 * @author lang : 2023-09-15
 */
public interface Permit {
    /**
     * 验证令牌
     *
     * @param token token详细信息
     *
     * @return 是否验证通过
     */
    Future<Boolean> token(String token);
}
