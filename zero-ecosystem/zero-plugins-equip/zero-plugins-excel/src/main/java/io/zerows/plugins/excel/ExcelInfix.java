package io.zerows.plugins.excel;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.application.YmlCore;
import io.zerows.program.Ux;
import io.zerows.sdk.plugins.Infix;

@Infusion
@Deprecated
@SuppressWarnings("all")
public class ExcelInfix implements Infix {

    private static final String NAME = "ZERO_EXCEL_POOL";
    private static final Cc<String, ExcelClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENT.pick(() -> Infix.init(YmlCore.inject.EXCEL,
            (config) -> null, // ExcelClient.createClient(vertx, config),
            ExcelInfix.class), name);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static ExcelClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    public static ExcelClient createClient() {
        return createClient(Ux.nativeVertx());
    }

    public static ExcelClient createClient(final Vertx vertx) {
        return Infix.init("excel", (config) -> null, ExcelInfix.class);
    }

    @Override
    public ExcelClient get() {
        return getClient();
    }
}
