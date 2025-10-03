package io.zerows.epoch.program.fn;

import io.vertx.core.Future;
import io.zerows.support.FnBase;
import io.zerows.epoch.program.Ut;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2023/4/27
 */
@SuppressWarnings("all")
final class ThenL {
    private ThenL() {
    }

    static <T> Future<List<T>> compressL(final List<Future<List<T>>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final List<T> result = new ArrayList<>();
            if (null != finished) {
                Ut.itList(finished.list(), (item, index) -> {
                    if (item instanceof List) {
                        final List<T> grouped = (List<T>) item;
                        if (!grouped.isEmpty()) {
                            result.addAll(grouped);
                        }
                    }
                });
            }
            return Future.succeededFuture(result);
        }).otherwise(FnBase.outAsync(ArrayList::new));
    }
}
