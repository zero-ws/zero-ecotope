package io.zerows.platform.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 内置存储器，上层对接 RegistryExtension 的注册器，用于从环境中提取所有的应用程序专用，应用是双设计
 * <pre><code>
 *     1. {@link HArk} 带详细配置的应用程序相关信息
 *     2. {@link HApp} 内置专用应用程序信息（目前是 HApp x 1）
 * </code></pre>
 *
 * @author lang : 2024-07-08
 */
public interface StoreArk extends OCache<HArk> {

    Cc<String, StoreArk> CC_SKELETON = Cc.open();

    static StoreArk of(final HBundle owner) {
        final String cacheKey = HBundle.id(owner, StoreArkAmbiguity.class);
        return CC_SKELETON.pick(() -> new StoreArkAmbiguity(owner), cacheKey);
    }

    static StoreArk of() {
        final HBundle owner = HPI.findBundle(StoreArk.class);
        return of(owner);
    }

    default StoreArk add(final Set<HArk> arkSet) {
        arkSet.stream().filter(Objects::nonNull).forEach(this::add);
        return this;
    }

    ConcurrentMap<String, HArk> valueMap();

    default StoreArk remove(final Set<HArk> arkSet) {
        arkSet.stream().filter(Objects::nonNull).forEach(this::remove);
        return this;
    }
}
