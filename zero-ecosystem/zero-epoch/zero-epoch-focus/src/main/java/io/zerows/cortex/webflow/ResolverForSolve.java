package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.annotations.Contract;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ResolverForSolve<T> implements Resolver<T> {

    @Contract
    private transient Solve<T> internalResolver;

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context, final WebEpsilon<T> income) {
        if (Objects.isNull(this.internalResolver)) {
            throw new _500ServerInternalException("[ R2MO ] Solve 实例为空");
        } else {
            // Default content from `configure`
            final String content = context.body().asString();
            log.info("[ ZERO ] ( Resolver ) Solve Type: {}, Content = {}",
                this.internalResolver.getClass(), content);
            final T processed = this.internalResolver.resolve(content);
            income.setValue(processed);
        }
        return income;
    }
}
