package io.zerows.component.expression;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class Regulation implements Serializable {

    private final List<Formula> formulas = new ArrayList<>();

    public Regulation() {
    }

    public Regulation add(final Formula formula) {
        Objects.requireNonNull(formula);
        this.formulas.add(formula);
        return this;
    }

    public Formula find(final String key) {
        Objects.requireNonNull(key);
        return this.formulas.stream()
            .filter(formula -> key.equals(formula.key()))
            .findAny().orElse(null);
    }

    public Future<JsonObject> run(final JsonObject params,
                                  final ConcurrentMap<String, Function<JsonObject, Future<JsonObject>>> futureMap) {
        final List<Future<JsonObject>> futures = new ArrayList<>();
        futureMap.forEach((ruleKey, ruleFn) -> {
            final Formula formula = this.find(ruleKey);
            if (Objects.nonNull(formula)) {
                /*
                 *  The operation should be executed inner
                 *  formula instead of outer action
                 */
                futures.add(formula.run(params, () -> ruleFn.apply(params)));
            }
        });
        return Future.join(new ArrayList<>(futures))
            /*
             * Here are response ignored execution
             * 1) When the formula is triggered, the response should be ignored
             * 2) The formula is often async mode, in this kind of situation, wait for all finished
             *
             * The Structure is as following:
             *
             * Start ->                    ->          -> ( param )
             *       -> Executor ( param ) -> finished
             *       -> Executor ( param ) -> finished
             *       -> Executor ( param ) -> finished
             *
             * All the executor ignored the response
             */
            .compose(finished -> Future.succeededFuture(params));
    }
}
