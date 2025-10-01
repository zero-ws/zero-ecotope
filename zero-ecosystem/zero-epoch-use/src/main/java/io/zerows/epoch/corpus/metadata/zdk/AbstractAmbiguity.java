package io.zerows.epoch.corpus.metadata.zdk;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.zdk.running.OCache;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 带有二义性的新组件，用于支持二选一的环境处理，其中包括
 * <pre><code>
 *     1. {@link Bundle} == null：OSGI 环境
 *     2. {@link Bundle} != null：OSGI 环境
 * </code></pre>
 * 其中此处的 Osgi 为 OSGI 环境下的 Osgi 对象，所有服务作用于此对象所需的基类
 *
 * @author lang : 2024-04-17
 */
public abstract class AbstractAmbiguity {

    private final Bundle bundle;

    protected AbstractAmbiguity(final Bundle bundle) {
        this.bundle = bundle;
        if (Objects.nonNull(bundle)) {
            // OSGI 环境专用
            final String cacheKey = Ut.Bnd.keyCache(bundle);
            final Set<Class<?>> selfSet = OCache.REGISTRY
                .getOrDefault(cacheKey, new HashSet<>());
            // 必须是 OCache
            if (OCache.class.isAssignableFrom(this.getClass())) {
                selfSet.add(this.getClass());
                OCache.REGISTRY.put(cacheKey, selfSet);
            }
        }
    }

    public Bundle caller() {
        return this.bundle;
    }
}
