package io.zerows.plugins.office.excel;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.sdk.metadata.plugins.Infix;

@Infusion
@SuppressWarnings("all")
public class ExcelInfix implements Infix {

    private static final String NAME = "ZERO_EXCEL_POOL";
    private static final Cc<String, ExcelClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENT.pick(() -> Infix.init(YmlCore.inject.EXCEL,
            (config) -> ExcelClient.createShared(vertx, config),
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
        return Infix.init("excel", (config) -> ExcelClient.createShared(vertx, config), ExcelInfix.class);
    }

    @Override
    public ExcelClient get() {
        return getClient();
    }
}
