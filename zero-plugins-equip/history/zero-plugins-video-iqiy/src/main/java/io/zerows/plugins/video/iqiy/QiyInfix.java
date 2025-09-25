package io.zerows.plugins.video.iqiy;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.core.annotations.Infusion;
import io.zerows.module.metadata.zdk.plugins.Infix;

@Infusion
@SuppressWarnings("all")
public class QiyInfix implements Infix {
    private static final String NAME = "ZERO_QIY_POOL";

    private static final Cc<String, QiyClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENT.pick(() -> Infix.init("qiy",
            (config) -> QiyClient.createShared(vertx, config),
            QiyInfix.class), name);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static QiyClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    @Override
    public QiyClient get() {
        return getClient();
    }
}
