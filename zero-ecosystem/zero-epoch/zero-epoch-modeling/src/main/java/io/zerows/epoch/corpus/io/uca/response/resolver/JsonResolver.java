package io.zerows.epoch.corpus.io.uca.response.resolver;

import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.component.serialization.ZeroType;
import io.zerows.epoch.corpus.io.zdk.mime.Resolver;
import io.zerows.epoch.corpus.model.Epsilon;
import io.zerows.epoch.program.Ut;

/**
 * InJson Resolver
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class JsonResolver<T> implements Resolver<T> {

    private static final Annal LOGGER = Annal.get(JsonResolver.class);

    @Override
    public Epsilon<T> resolve(final RoutingContext context,
                              final Epsilon<T> income) {
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
