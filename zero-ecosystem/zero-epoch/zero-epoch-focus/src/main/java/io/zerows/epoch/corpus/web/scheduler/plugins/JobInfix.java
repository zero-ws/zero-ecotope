package io.zerows.epoch.corpus.web.scheduler.plugins;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.sdk.metadata.plugins.Infix;

@Infusion
@SuppressWarnings("unchecked")
public class JobInfix implements Infix {

    private static final String NAME = "ZERO_JOB_POOL";
    private static final Cc<String, JobClient> CC_CLIENTS = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENTS.pick(() -> Infix.init("job",
            (config) -> JobClient.createShared(vertx, config.getJsonObject("client")),
            JobInfix.class
        ), name);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static JobClient getClient() {
        return CC_CLIENTS.get(NAME);
    }

    @Override
    public JobClient get() {
        return getClient();
    }
}
