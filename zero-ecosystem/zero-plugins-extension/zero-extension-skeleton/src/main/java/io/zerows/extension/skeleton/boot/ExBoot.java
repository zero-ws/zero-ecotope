package io.zerows.extension.skeleton.boot;

import io.r2mo.spi.SPI;
import io.zerows.cortex.sdk.HQBE;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.extension.skeleton.spi.ExActivity;
import io.zerows.extension.skeleton.spi.ExApp;
import io.zerows.extension.skeleton.spi.ExArbor;
import io.zerows.extension.skeleton.spi.UiApeak;
import io.zerows.extension.skeleton.spi.UiApeakMy;
import io.zerows.extension.skeleton.spi.UiForm;
import io.zerows.platform.constant.VString;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.development.HMaven;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-31
 */
@Slf4j
class ExBoot {

    private static final List<Class<?>> SPI_SET = new ArrayList<>() {
        {
            this.add(HQBE.class);
            // -- 扩展接口处理
            this.add(ExActivity.class);
            this.add(ExApp.class);
            this.add(ExArbor.class);
            // -- UI 处理
            this.add(UiForm.class);
            this.add(UiApeak.class);
            this.add(UiApeakMy.class);
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

    static void vModule() {
        log.info("{} 加载模块 ID 集合：", HActor.COLOR_EXTENSION);
        final Set<Class<?>> scanned = OCacheClass.entireValue();
        scanned.forEach(item -> {
            if (Arrays.asList(item.getInterfaces()).contains(HMaven.class)) {
                final String value = Ut.field(item, "BUNDLE_SYMBOLIC_NAME");
                if (Objects.nonNull(value)) {
                    log.info("{}    - {}", HActor.COLOR_EXTENSION, value);
                }
            }
        });
    }
}
