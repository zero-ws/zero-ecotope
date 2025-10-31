package io.zerows.plugins.excel.component;

import java.util.concurrent.ConcurrentMap;

public class ExValuePure implements ExValue {

    @Override
    @SuppressWarnings("all")
    public Object to(Object value, final ConcurrentMap<String, String> paramMap) {
        return value;
    }
}
