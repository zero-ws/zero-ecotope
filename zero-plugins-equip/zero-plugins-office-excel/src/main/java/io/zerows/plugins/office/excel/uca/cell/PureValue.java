package io.zerows.plugins.office.excel.uca.cell;

import java.util.concurrent.ConcurrentMap;

public class PureValue implements ExValue {

    @Override
    @SuppressWarnings("all")
    public Object to(Object value, final ConcurrentMap<String, String> paramMap) {
        return value;
    }
}
