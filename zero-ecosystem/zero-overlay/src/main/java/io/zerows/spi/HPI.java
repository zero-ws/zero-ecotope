package io.zerows.spi;

import io.r2mo.spi.SPI;
import io.zerows.platform.exception._11000Exception404SPINotFound;
import io.zerows.specification.development.compiled.HBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 直接从 SPI 继承接口，对 SPI 功能进行扩展，主要追加功能支持：是否覆盖默认的 SPI 单独执行器
 *
 * @author lang : 2025-10-02
 */
public interface HPI extends SPI {

    Logger log = LoggerFactory.getLogger(HPI.class);

    static <T> T findOverwrite(final Class<T> clazz) {
        final List<T> found = SPI.findMany(clazz);
        return findOverwrite(found, clazz);
    }

    static <T> T findOverwrite(final Class<T> clazz, final Class<?> classLoader) {
        final List<T> found = SPI.findMany(clazz, classLoader);
        return findOverwrite(found, clazz);
    }

    static HBundle findBundle(final Class<?> clazzLoader) {
        return findOverwrite(HBundle.class, clazzLoader);
    }

    private static <T> T findOverwrite(final List<T> found, final Class<T> clazzCls) {
        if (2 < found.size()) {
            log.error("[ ZERO ] 此方法要求 SPI 只能有一个或两个实现类。");
            throw new _11000Exception404SPINotFound(clazzCls);
        }
        // 只找到唯一的一个实现
        if (1 == found.size()) {
            return found.getFirst();
        }
        // 找到两个实现，要返回包名不是 io.zerows 的（默认）
        return found.stream()
            .filter(it -> !it.getClass().getPackageName().startsWith("io.zerows"))
            .findFirst()
            .orElseThrow(() -> new _11000Exception404SPINotFound(clazzCls));
    }
}
