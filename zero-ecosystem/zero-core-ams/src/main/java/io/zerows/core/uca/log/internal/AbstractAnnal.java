package io.zerows.core.uca.log.internal;

import io.zerows.ams.util.HUt;
import io.zerows.core.uca.log.Annal;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/25
 */
public abstract class AbstractAnnal implements Annal {

    protected void log(final Supplier<Boolean> fnPre,
                       final BiConsumer<String, Object> fnLog,
                       final String message,
                       final Object... rest) {
        if (fnPre.get()) {
            final String formatted = HUt.fromMessageB(message, rest);
            fnLog.accept(formatted, null);
        }
    }

    protected void log(final BiConsumer<String, Object> fnLog,
                       final String message,
                       final Object... rest) {
        this.log(() -> true, fnLog, message, rest);
    }
}
