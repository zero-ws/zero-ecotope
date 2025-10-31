package io.zerows.plugins.excel.component;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * {UUID} Processing
 */
public class ExValueUuid implements ExValue {

    @Override
    @SuppressWarnings("all")
    public Object to(final Object value, final ConcurrentMap<String, String> paramMap) {
        return UUID.randomUUID().toString();
    }
}
