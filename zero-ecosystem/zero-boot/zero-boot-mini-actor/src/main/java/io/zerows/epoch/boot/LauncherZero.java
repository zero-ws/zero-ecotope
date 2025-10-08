package io.zerows.epoch.boot;

import io.vertx.core.Vertx;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cosmic.EnergyVertx;
import io.zerows.cosmic.EnergyVertxService;
import io.zerows.epoch.basicore.NodeNetwork;
import io.zerows.epoch.management.OCacheNode;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 标准容器 Service / Application 启动器
 *
 * @author lang : 2023-05-30
 */
public class LauncherZero implements HLauncher<Vertx> {

    private static final EnergyVertx SERVICE = new EnergyVertxService();
    private static final ConcurrentMap<String, Vertx> STORED_DATA = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<String, Vertx> store() {
        if (STORED_DATA.isEmpty()) {
            final StoreVertx storeVertx = StoreVertx.of();
            final Set<String> names = storeVertx.keys();
            names.forEach(name -> STORED_DATA.put(name, storeVertx.vertx(name)));
        }
        return STORED_DATA;
    }

    @Override
    public <T extends HConfig> void start(final HConfig.HOn<T> on, final Consumer<Vertx> server) {
        final NodeNetwork network = OCacheNode.of().network();

        final HBundle found = HPI.findBundle(this.getClass());
        SERVICE.startAsync(found, network).onComplete(cached -> {
            if (cached.failed()) {
                Ut.Log.boot(this.getClass()).fatal(cached.cause());
                return;
            }


            // 存储引用专用
            final StoreVertx storeVertx = cached.result();
            final Set<String> names = storeVertx.keys();

            names.forEach(name -> {
                final Vertx vertx = storeVertx.vertx(name);
                if (Objects.nonNull(vertx)) {
                    server.accept(vertx);
                }
            });
        });
    }

    @Override
    public <T extends HConfig> void stop(final HConfig.HOff<T> off, final Consumer<Vertx> server) {
        // 等待实现
    }
}
