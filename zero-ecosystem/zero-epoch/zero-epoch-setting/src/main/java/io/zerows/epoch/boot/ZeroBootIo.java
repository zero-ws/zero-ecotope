package io.zerows.epoch.boot;

import io.r2mo.SourceReflect;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.spi.BootIo;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * @author lang : 2023-05-30
 */
public class ZeroBootIo implements BootIo {
    @Override
    @SuppressWarnings("unchecked")
    public <T> HLauncher<T> launcher() {
        final ZeroStation station = ZeroStation.singleton();
        final Class<?> launcher = Objects.requireNonNull(station.boot()).launcher();
        if (Objects.isNull(launcher)) {
            // 非自定义启动器
            return HPI.findOneOf(HLauncher.class);
        }
        // 自定义启动器
        return SourceReflect.singleton(launcher);
    }

    @Override
    public HEnergy energy(final Class<?> target, final String[] args) {
        // 内置实现配置，Vertx直接从本身实现中处理
        final ZeroStation station = ZeroStation.singleton(target, args);
        // 内置绑定
        return Objects.requireNonNull(station.boot()).energy();
    }
}
