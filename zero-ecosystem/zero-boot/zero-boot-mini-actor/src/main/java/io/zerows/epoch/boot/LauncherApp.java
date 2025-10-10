package io.zerows.epoch.boot;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cosmic.EnergyVertx;
import io.zerows.cosmic.EnergyVertxService;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.platform.management.StoreSetting;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

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
@SPID(priority = 216)
@Slf4j
public class LauncherApp implements HLauncher<Vertx> {

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
    public void start(final HEnergy energy, final Consumer<Vertx> server) {

        final HBundle found = HPI.findBundle(this.getClass());
        final NodeNetwork network = StoreSetting.of(found).getNetwork(energy.setting());

        SERVICE.startAsync(found, network).onComplete(cached -> {
            if (cached.failed()) {
                log.error("[ ZERO ] 系统启动异常！", cached.cause());
                return;
            }


            // 存储引用专用
            final StoreVertx storeVertx = StoreVertx.of();
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
    public void stop(final HEnergy energy, final Consumer<Vertx> server) {
        // 等待实现
    }
}
