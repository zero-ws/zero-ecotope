package io.zerows.extension.module.rbac.component;

import io.vertx.core.Future;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Fx;

import java.util.*;

/**
 * @author lang : 2024-09-14
 */
abstract class ScClockBase<T> extends AbstractAmbiguity implements ScClock<T> {

    private final String poolName;

    ScClockBase(final HBundle bundle, final String poolName) {
        super(bundle);
        Objects.requireNonNull(poolName);
        this.poolName = poolName;
    }

    @Override
    public Future<T> get(final String key, final boolean isOnce) {
        if (isOnce) {
            return this.ofCache().remove(key);
        } else {
            return this.ofCache().find(key);
        }
    }

    @Override
    public Future<T> put(final String key, final T value, final String... moreKeys) {
        if (0 == moreKeys.length) {
            return this.ofCache().put(key, value, this.configTtl());
        } else {
            final Set<String> keySet = new HashSet<>(Arrays.asList(moreKeys));
            keySet.add(key);
            return this.ofCache().putMulti(keySet, value, this.configTtl());
        }
    }

    @Override
    public Future<Boolean> remove(final String... keys) {
        final List<Future<Boolean>> waitingQ = new ArrayList<>();
        Arrays.asList(keys).forEach(key -> waitingQ.add(this.ofCache().remove(key).compose(nil -> Ux.futureT())));
        return Fx.combineB(waitingQ);
    }

    @Override
    public HMM<String, T> ofCache() {
        return HMM.of(this.poolName);
    }
}
