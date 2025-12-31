package io.zerows.plugins.integration.wechat;

import io.vertx.core.json.JsonObject;

import java.io.Serializable;

/**
 * @author lang : 2024-07-12
 */
public class WechatConfig implements Serializable {

    static String CONFIG_KEY = "wechat";

    private WechatConfig(final JsonObject config) {

    }

    static WechatConfig create(final JsonObject config) {
        return new WechatConfig(config);
    }

    static WechatConfig create() {
        return null;
    }
}
