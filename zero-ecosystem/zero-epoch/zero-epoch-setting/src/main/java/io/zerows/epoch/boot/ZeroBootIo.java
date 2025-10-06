package io.zerows.epoch.boot;

import io.r2mo.SourceReflect;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.spi.BootIo;

import java.util.Objects;

/**
 * @author lang : 2023-05-30
 */
public class ZeroBootIo implements BootIo {

    @Override
    public <T> HLauncher<T> launcher() {
        /*
         * 混合启动器，可开启两种启动模式
         * 1. Micro：微服务模式
         * 2. Zero：单机模式
         */
        final ZeroStation store = ZeroStation.singleton();
        final Class<?> launcher = store.boot().launcher();
        Objects.requireNonNull(launcher);
        return SourceReflect.singleton(launcher);
    }

    @Override
    public HEnergy energy(final Class<?> target, final String[] args) {
        // 内置实现配置，Vertx直接从本身实现中处理
        final ZeroStation store = ZeroStation.singleton(target, args);
        // 内置绑定
        final HBoot boot = store.boot();
        return boot.energy();
    }
}
