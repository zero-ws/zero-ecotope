package io.zerows.platform.metadata;

import io.vertx.core.Future;
import io.zerows.platform.exception._40104Exception409RegistryDuplicated;
import io.zerows.support.base.UtBase;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.specification.vital.HOI;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2023-06-07
 */
class KPivotTool {
    static void fail(final Class<?> clazz,
                     final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            throw new _40104Exception409RegistryDuplicated(stored.size());
        }
    }

    static Future<Boolean> failAsync(final Class<?> clazz,
                                     final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            return Future.failedFuture(new _40104Exception409RegistryDuplicated(stored.size()));
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
            final HArk found = UtBase.elementFind(ownerList,
                item -> source.app().equals(item.app()));
            if (Objects.nonNull(found)) {
                source.apply(found);
            }
        });
        // 3. 构造线程安全的集合
        return Collections.synchronizedSet(sources);
    }
}
