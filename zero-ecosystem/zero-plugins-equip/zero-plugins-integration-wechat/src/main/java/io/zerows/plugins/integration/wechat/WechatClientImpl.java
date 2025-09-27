package io.zerows.plugins.integration.wechat;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2024-07-12
 */
public class WechatClientImpl implements WechatClient {

    private transient final Vertx vertx;
    private transient final WechatConfig config;

    WechatClientImpl(final Vertx vertx, final WechatConfig config) {
        this.vertx = vertx;
        this.config = config;
    }

    @Override
    public WechatClient init(final JsonObject params) {
        return null;
    }
}
