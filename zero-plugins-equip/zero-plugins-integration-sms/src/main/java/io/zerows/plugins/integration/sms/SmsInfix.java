package io.zerows.plugins.integration.sms;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.core.annotations.Infusion;
import io.zerows.module.metadata.zdk.plugins.Infix;

@Infusion
@SuppressWarnings("all")
public class SmsInfix implements Infix {

    private static final String NAME = "ZERO_SMS_ALI_POOL";

    private static final Cc<String, SmsClient> CC_CLIENT = Cc.open();

    public static void init(final Vertx vertx) {
        CC_CLIENT.pick(() -> Infix.init(SmsConfig.CONFIG_KEY,
            (config) -> SmsClient.createShared(vertx),
            SmsInfix.class), NAME);
    }

    public static SmsClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    @Override
    public SmsClient get() {
        return getClient();
    }
}
