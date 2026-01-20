package io.zerows.plugins.security.oauth2.server.service;

import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;
import lombok.extern.slf4j.Slf4j;

/**
 * 令牌服务端点服务 (Dispatcher)
 * <p>
 * 职责：
 * 1. 接收令牌请求
 * 2. 解析 grant_type
 * 3. 路由到对应的 Granter 策略实现
 */
@Slf4j
public class TokenService implements TokenStub {

    @Override
    public Future<JsonObject> tokenAsync(final JsonObject request) {
        // 1. 获取并解析授权模式
        final String grantTypeStr = request.getString(OAuth2Constant.GRANT_TYPE);
        log.info("{} 收到令牌请求，GrantType = {}", OAuth2Constant.K_PREFIX, grantTypeStr);

        // 2. 将字符串转换为枚举
        final OAuth2GrantType grantType = OAuth2GrantType.from(grantTypeStr);

        // 3. 尝试获取对应的授权器 (Granter)
        final Granter granter = (grantType != null) ? Granter.of(grantType) : null;

        // 4. 路由分发
        if (granter != null) {
            // 委托给具体的策略实现类 (如 GranterClientCredentials) 执行
            return granter.grantAsync(request);
        }

        // 5. 不支持的模式处理
        return Future.failedFuture(new _501NotSupportException(
            OAuth2Constant.K_PREFIX + " 暂不支持该授权模式: " + (grantTypeStr == null ? "null" : grantTypeStr)
        ));
    }
}