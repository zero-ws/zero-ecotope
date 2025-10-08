package io.zerows.spi;

import io.r2mo.spi.SPI;
import io.zerows.platform.constant.VString;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.modeler.AtomNs;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 直接从 SPI 继承接口，对 SPI 功能进行扩展，主要追加功能支持：是否覆盖默认的 SPI 单独执行器
 *
 * @author lang : 2025-10-02
 */
@Slf4j
public final class HPI extends SPI {

    public static HBundle findBundle(final Class<?> clazzLoader) {
        return SPI.findOverwrite(HBundle.class, clazzLoader);
    }

    public static void monitorOf() {
        final List<Class<?>> spiSet = new ArrayList<>();
        spiSet.add(AtomNs.class);

        log.info("[ ZERO ] SPI 监控详情：");
        for (final Class<?> spiClass : spiSet) {
            final List<?> implementations = SPI.findMany(spiClass);
            final String implNames = implementations.isEmpty()
                ? VString.EMPTY
                : implementations.stream()
                .map(impl -> impl.getClass().getName())
                .distinct()
                .collect(Collectors.joining(", "));
            log.info("[ ZERO ]    \uD83D\uDCCC {} = [{}]", spiClass.getName(), implNames);
        }
    }
}
