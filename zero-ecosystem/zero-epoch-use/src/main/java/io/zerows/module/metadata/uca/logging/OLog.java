package io.zerows.module.metadata.uca.logging;

import io.zerows.epoch.constant.VString;
import io.zerows.core.uca.log.Annal;
import io.zerows.module.metadata.cache.CStore;
import io.zerows.specification.atomic.HLogger;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * OSGI 专用日志接口，用于基础日志信息，和 OSGI Framework 相关，底层虽然采用了 {@link Annal}，但内置使用第二层用于日志输出
 * <pre><code>
 *     1. {@link Annal} 主要用于在开发过程中输出日志，通常采用类似
 *        Annal LOGGER = Annal.get(XXX.class);
 *        Annal LOGGER = new Log4JAnnal(XXX.class);
 *     2. {@link OLog} 则负责快速日志处理
 *        KLog.of(XXX.class, "name").info("message");
 * </code></pre>
 *
 * @author lang : 2024-04-17
 */
public interface OLog extends HLogger {

    static OLog of(final Class<?> clazz, final String name) {
        Objects.requireNonNull(clazz);
        return CStore.CC_LOG.pick(() -> new LogConsole(clazz, name), clazz.getName() + VString.SLASH + name);
    }

    @Override
    default void info(final boolean condition, final String key, final Object... args) {
        if (condition) {
            this.info(key, args);
        }
    }

    default void info(final Supplier<Boolean> condition, final String key, final Object... args) {
        this.info(condition.get(), key, args);
    }
}
