package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.support.Ut;
import io.zerows.weaver.ZeroType;
import lombok.extern.slf4j.Slf4j;

/**
 * InJson Resolver
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
@Slf4j
public class ResolverJson<T> implements Resolver<T> {

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context,
                                 final WebEpsilon<T> income) {
        // InJson Resolver
        final String content = context.body().asString();
        if (Ut.isNotNil(content)) {
            log.info("[ ZERO ] ( Resolver ) KIncome Type: {}, Content = [0;37m{}[m",
                income.getArgType().getName(), content);
        }
        if (Ut.isNil(content)) {
            // Default Value set for BodyParam
            final T defaultValue = (T) income.getDefaultValue();
            income.setValue(defaultValue);
        } else {
            final Object result = ZeroType.value(income.getArgType(), content);
            if (null != result) {
                income.setValue((T) result);
            }
        }
        return income;
    }
}
