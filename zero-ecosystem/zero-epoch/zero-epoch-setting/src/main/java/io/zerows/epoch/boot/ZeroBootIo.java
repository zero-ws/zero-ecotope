package io.zerows.epoch.boot;

import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.spi.BootIo;

/**
 * @author lang : 2023-05-30
 */
public class ZeroBootIo implements BootIo {
    @Override
    public HBoot boot(final Class<?> upClass) {
        // final ZeroStation station = ZeroStation.singleton();
        // 自定义启动器
        return null; // SourceReflect.singleton(launcher);
    }

    @Override
    public HEnergy energy(final Class<?> target, final String[] args) {
        // 内置实现配置，Vertx直接从本身实现中处理
        // final ZeroStation station = ZeroStation.singleton(target, args);
        // 内置绑定
        // return Objects.requireNonNull(station.boot()).energy();
        return null;
    }
}
