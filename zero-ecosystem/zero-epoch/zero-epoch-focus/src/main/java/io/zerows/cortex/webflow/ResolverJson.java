package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.LogOf;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.support.Ut;
import io.zerows.weaver.ZeroType;

/**
 * InJson Resolver
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class ResolverJson<T> implements Resolver<T> {

    private static final LogOf LOGGER = LogOf.get(ResolverJson.class);

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context,
                                 final WebEpsilon<T> income) {
        // InJson Resolver
        final String content = context.body().asString();
        LOGGER.info(Ut.isNotNil(content), "( Resolver ) KIncome Type: {0}, Content = \u001b[0;37m{1}\u001b[m",
            income.getArgType().getName(), content);
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
