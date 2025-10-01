package io.zerows.plugins.integration.wechat;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.sdk.metadata.plugins.InfixClient;

/**
 * @author lang : 2024-07-12
 */
public interface WechatClient extends InfixClient<WechatClient> {

    static WechatClient createShared(final Vertx vertx) {
        return new WechatClientImpl(vertx, WechatConfig.create());
    }

    static WechatClient createShared(final Vertx vertx, final JsonObject config) {
        return new WechatClientImpl(vertx, WechatConfig.create(config));
    }
}
