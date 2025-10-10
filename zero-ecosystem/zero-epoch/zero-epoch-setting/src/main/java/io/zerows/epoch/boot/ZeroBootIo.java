package io.zerows.epoch.boot;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.zerows.platform.management.StoreSetting;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HSetting;
import io.zerows.spi.BootIo;

import java.util.Objects;

/**
 * @author lang : 2023-05-30
 */
public class ZeroBootIo implements BootIo {

    @Override
    public HBoot boot(final Class<?> bootCls) {

        final HSetting setting = this.of(bootCls);

        if (Objects.isNull(setting)) {
            throw new _500ServerInternalException("[ ZERO ] 配置数据丢失！");
        }

        return ZeroBoot.of(setting).mainClass(bootCls);
    }

    private HSetting of(final Class<?> bootCls) {
        HSetting setting = StoreSetting.of().getBy(bootCls);
        if (Objects.isNull(setting)) {
            setting = ZeroPower.of().compile(bootCls);
        }
        return setting;
    }

    @Override
    public HEnergy energy(final Class<?> bootCls, final String[] args) {

        final HSetting setting = this.of(bootCls);

        final ZeroEnergy energy = ZeroEnergy.of(setting);

        return energy.args(args);
    }
}
