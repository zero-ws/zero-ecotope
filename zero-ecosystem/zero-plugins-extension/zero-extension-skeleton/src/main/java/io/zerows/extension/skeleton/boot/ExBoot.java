package io.zerows.extension.skeleton.boot;

import io.r2mo.spi.SPI;
import io.zerows.extension.skeleton.spi.ExActivity;
import io.zerows.extension.skeleton.spi.ExApp;
import io.zerows.extension.skeleton.spi.ExArbor;
import io.zerows.platform.constant.VString;
import io.zerows.specification.configuration.HActor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-31
 */
@Slf4j
class ExBoot {

    private static final List<Class<?>> SPI_SET = new ArrayList<>() {
        {
            this.add(ExActivity.class);
            this.add(ExApp.class);
            this.add(ExArbor.class);
        }
    };

    static void vLog() {
        log.info("{} 扩展模块 SPI 监控详情：", HActor.COLOR_EXTENSION);
        for (final Class<?> spiClass : SPI_SET) {
            final List<?> implementations = SPI.findMany(spiClass);
            final String implNames = implementations.isEmpty()
                ? VString.EMPTY
                : implementations.stream()
                .map(impl -> impl.getClass().getName())
                .distinct()
                .collect(Collectors.joining(", "));
            log.info("{}    \uD83E\uDD4F {} = [{}]", HActor.COLOR_EXTENSION, spiClass.getName(), implNames);
        }
    }
}
