package io.zerows.plugins.integration.wechat;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.corpus.metadata.zdk.plugins.Infix;

/**
 * @author lang : 2024-07-12
 */
@Infusion
@SuppressWarnings("all")
public class WechatInfix implements Infix {
    private static final String NAME = "ZERO_WECHAT_POOL";

    private static final Cc<String, WechatClient> CC_CLIENT = Cc.open();

    public static void init(final Vertx vertx) {
        CC_CLIENT.pick(() -> Infix.init(WechatConfig.CONFIG_KEY,
            (config) -> WechatClient.createShared(vertx),
            WechatInfix.class), NAME);
    }

    public static WechatClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    @Override
    public WechatClient get() {
        return getClient();
    }
}
