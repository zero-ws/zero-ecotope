package io.zerows.core.running.boot;

import io.vertx.core.Future;
import io.zerows.core.exception.boot.DuplicateRegistryException;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.vital.HOI;
import io.zerows.ams.util.HUt;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2023-06-07
 */
class KPivotT {
    static void fail(final Class<?> clazz,
                     final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            throw new DuplicateRegistryException(clazz, stored.size());
        }
    }

    static Future<Boolean> failAsync(final Class<?> clazz,
                                     final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            return Future.failedFuture(new DuplicateRegistryException(clazz, stored.size()));
        } else {
            return Future.succeededFuture(Boolean.TRUE);
        }
    }

    static Set<HArk> combine(final Set<HArk> sources, final Set<HArk> extensions) {
        sources.forEach(source -> {
            // 1. 先做租户过滤
            final HOI owner = source.owner();
            final List<HArk> ownerList = extensions.stream()
                .filter(item -> item.owner().equals(owner))
                .collect(Collectors.toList());

            // 2. 再做二次查找到唯一记录
            final HArk found = HUt.elementFind(ownerList,
                item -> source.app().equals(item.app()));
            if (Objects.nonNull(found)) {
                source.apply(found);
            }
        });
        // 3. 构造线程安全的集合
        return Collections.synchronizedSet(sources);
    }
}
