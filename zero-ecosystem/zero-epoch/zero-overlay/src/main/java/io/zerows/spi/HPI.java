package io.zerows.spi;

import io.r2mo.spi.FactoryDBAction;
import io.r2mo.spi.FactoryIo;
import io.r2mo.spi.FactoryObject;
import io.r2mo.spi.FactoryWeb;
import io.r2mo.spi.SPI;
import io.zerows.platform.constant.VString;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.modeling.operation.HLoad;
import io.zerows.spi.modeler.AtomNs;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 直接从 SPI 继承接口，对 SPI 功能进行扩展，主要追加功能支持：是否覆盖默认的 SPI 单独执行器
 *
 * @author lang : 2025-10-02
 */
@Slf4j
public final class HPI extends SPI {

    private static final List<Class<?>> SPI_SET = new ArrayList<>() {
        {
            // R2MO 部分
            this.add(FactoryObject.class);
            this.add(FactoryIo.class);
            this.add(FactoryDBAction.class);
            this.add(FactoryWeb.class);
            // 应用部分
            this.add(AtomNs.class);
            this.add(BootIo.class);
            // 高阶部分
            this.add(HBundle.class);
            this.add(HLauncher.class);
            this.add(HLoad.class);
        }
    };

    public static void registry(final Class<?>... spiArray) {
        SPI_SET.addAll(Arrays.asList(spiArray));
    }

    public static HBundle findBundle(final Class<?> clazzLoader) {
        return SPI.findOverwrite(HBundle.class, clazzLoader);
    }

    public static void vLog() {

        log.info("[ ZERO ] SPI 监控详情：");
        for (final Class<?> spiClass : SPI_SET) {
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
