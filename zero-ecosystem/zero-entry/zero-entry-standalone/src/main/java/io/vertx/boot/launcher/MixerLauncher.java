package io.vertx.boot.launcher;

import io.vertx.core.Vertx;
import io.zerows.epoch.enums.EmApp;
import io.zerows.core.running.boot.KEnvironment;
import io.zerows.core.util.Ut;
import io.zerows.core.web.container.store.BootStore;
import io.zerows.specification.access.HLauncher;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;

import java.util.function.Consumer;

/**
 * @author lang : 2023-05-30
 */
public class MixerLauncher implements HLauncher<Vertx> {
    private static final BootStore STORE = BootStore.singleton();
    private transient final HLauncher<Vertx> micro;
    private transient final HLauncher<Vertx> zero;

    public MixerLauncher() {
        this.micro = Ut.singleton(MicroLauncher.class);
        this.zero = Ut.singleton(ZeroLauncher.class);
    }

    @Override
    public <T extends HConfig> void start(final HConfig.HOn<T> on, final Consumer<Vertx> server) {
        // 环境变量处理提前
        KEnvironment.initialize();

        final HBoot boot = STORE.boot();
        if (EmApp.Type.APPLICATION == boot.app()) {
            this.zero.start(on, server);
        } else {
            this.micro.start(on, server);
        }
    }

    @Override
    public <T extends HConfig> void stop(final HConfig.HOff<T> off, final Consumer<Vertx> server) {
        final HBoot boot = STORE.boot();
        if (EmApp.Type.APPLICATION == boot.app()) {
            this.zero.stop(off, server);
        } else {
            this.micro.stop(off, server);
        }
    }
}
