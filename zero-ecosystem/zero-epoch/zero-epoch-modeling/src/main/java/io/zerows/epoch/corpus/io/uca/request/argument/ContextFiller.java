package io.zerows.epoch.corpus.io.uca.request.argument;

import io.vertx.ext.web.RoutingContext;
import io.zerows.weaver.ZeroType;

import java.util.Map;
import java.util.Objects;

public class ContextFiller implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        final Map<String, Object> data = context.data();
        final Object value = data.get(name);
        if (Objects.isNull(value)) {
            return null;
        }
        if (paramType == value.getClass()) {
            return value;
        } else {
            final String valueStr = value.toString();
            return ZeroType.value(paramType, valueStr);
        }
    }
}
