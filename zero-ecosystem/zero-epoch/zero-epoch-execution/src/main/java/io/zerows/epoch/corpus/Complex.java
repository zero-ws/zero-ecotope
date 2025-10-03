package io.zerows.epoch.corpus;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.FnBase;
import io.zerows.metadata.program.KRef;
import io.zerows.component.qr.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Complex {
    /**
     * This function executed on input reference
     * 1) If predicate = true, return executor(Tool) -> Future<Tool>
     * 2) If predicate = false, return Future<Tool>
     * return Tool in other situations.
     *
     * @param input     input Tool reference
     * @param predicate Check whether the Tool reference is ok for `executor`
     * @param executor  The continue executor on Tool
     * @param <T>       The entity object, now it support JsonObject/JsonArray
     *
     * @return Future<Tool> for result
     */
    static <T> Future<T> complex(final T input, final Predicate<T> predicate, final Supplier<Future<T>> executor) {
        if (Objects.isNull(input)) {
            return ToCommon.future(null);
        } else {
            if (Objects.isNull(executor)) {
                return ToCommon.future(input);
            } else {
                if (Objects.isNull(predicate)) {
                    return executor.get();
                } else {
                    if (predicate.test(input)) {
                        return executor.get();
                    } else {
                        return ToCommon.future(input);
                    }
                }
            }
        }
    }

    /**
     * The workflow for current function
     * 1 - Pagination `first` will be consumed by `pageConsumer` first,
     * 2 - Get the first response by `responseBuilder` function to get first result.
     * 3 - Generate `total` by `totalConsumer` function to get the total records number.
     * 4 - Based on Pagination ( include total ), generate Set pagination.
     * 5 - Each group call `pageConsumer` again ( exclude first )
     * 6 - Get List<R> result, call fnReduce to generate new R
     *
     * @param first           The pagination object ( Pager + total )
     * @param totalConsumer   The total consumer that consume the response of 1st to generate `Pagination`
     * @param pageConsumer    The default consumer for Pagination to get `Pagination`
     * @param responseBuilder The builder for result building
     * @param fnReduce        The combine function to calculate R + R -> R such as List.addAll(list)
     * @param <T>             The response of `pageConsumer`
     * @param <R>             The final result of `responseBuilder`
     *
     * @return Final result for batch
     */
    static <T, R> Future<R> complex(final Pagination first,
                                    final Function<T, Future<Integer>> totalConsumer,
                                    final Function<Pagination, Future<T>> pageConsumer,
                                    final Function<T, Future<R>> responseBuilder,
                                    final BinaryOperator<R> fnReduce) {
        /*
         * First response to get based on `pageConsumer`
         */
        final KRef firstResult = new KRef();
        return pageConsumer.apply(first)
            /*
             * Get response R,
             */
            .compose(response -> responseBuilder.apply(response)
                .compose(firstResult::future)
                .compose(nil -> totalConsumer.apply(response))
                .compose(total -> {
                    first.setTotal(total);
                    return Ux.future(first);
                })
            )
            .compose(Pagination::scatterAsync)
            .compose(pageSet -> {
                if (pageSet.isEmpty()) {
                    /*
                     * No more page
                     */
                    return Ux.future(firstResult.get());
                } else {
                    final List<Future<R>> futures = new ArrayList<>();
                    pageSet.stream()
                        .map(each -> pageConsumer.apply(each).compose(responseBuilder))
                        .forEach(futures::add);
                    return FnBase.combineT(futures).compose(list -> {
                        final R result = list.stream().reduce(fnReduce).orElse(null);
                        final R firstRef = firstResult.get();
                        return Ux.future(fnReduce.apply(firstRef, result));
                    });
                }
            })
            .otherwise(Ux.otherwise(() -> null));
    }

    static Function<Pagination, Future<JsonArray>> complex(final Function<JsonObject, Future<Integer>> total, final Function<Pagination, Future<JsonObject>> page, final Function<JsonObject, Future<JsonArray>> result) {
        return first -> Complex.complex(first, total, page, result, JsonArray::addAll);
    }
}
