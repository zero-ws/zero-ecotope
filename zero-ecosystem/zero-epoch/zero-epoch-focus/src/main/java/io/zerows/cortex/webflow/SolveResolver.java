package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.annotations.Contract;

import java.util.Objects;

public class SolveResolver<T> implements Resolver<T> {

    private static final Annal LOGGER = Annal.get(SolveResolver.class);
    @Contract
    private transient Solve<T> internalResolver;

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context, final WebEpsilon<T> income) {
        if (Objects.isNull(this.internalResolver)) {
            throw new _500ServerInternalException("[ R2MO ] Solve 实例为空");
        } else {
            // Default content from `context`
            final String content = context.body().asString();
            LOGGER.info("( Resolver ) Solve Type: {0}, Content = {1}",
                this.internalResolver.getClass(), content);
            final T processed = this.internalResolver.resolve(content);
            income.setValue(processed);
        }
        return income;
    }
}
