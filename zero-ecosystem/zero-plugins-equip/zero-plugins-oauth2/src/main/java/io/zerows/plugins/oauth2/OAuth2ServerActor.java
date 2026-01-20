package io.zerows.plugins.oauth2;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

@Actor(value = "oauth2", sequence = -160)
public class OAuth2ServerActor extends AbstractHActor {
    /**
     * 上层 Actor 会调用此静态方法来注册当前环境中的 keystore 核心数据，提供给 /oauth2/jwks 端点使用
     *
     * @param vertx        Vertx 实例
     * @param keystoreData 生成好的内容
     */
    public static void keystoreOf(final Vertx vertx, final JsonObject keystoreData) {
        OAuth2Manager.of().keystoreOf(vertx, keystoreData);
    }

    public static JsonObject keystoreOf() {
        return OAuth2Manager.of().keystoreOf();
    }

    public static OAuth2Security securityOf(final Vertx vertx, final JsonObject securityJ) {
        OAuth2Manager.of().securityOf(vertx, securityJ);
        return OAuth2Manager.of().securityOf();
    }

    public static OAuth2Security securityOf() {
        return OAuth2Manager.of().securityOf();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (Objects.isNull(config)) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        this.vLog("[ OAuth2 ] OAuth2管理器初始化完成，配置：{}", config.options());

        OAuth2Manager.of().configOf(vertxRef, config);
        this.vLog("[ OAuth2 ] OAuth2管理配置已加载完成！！");
        return Future.succeededFuture(Boolean.TRUE);
    }
}
