package io.zerows.extension.module.rbac.component;

import io.vertx.core.Future;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.program.Ux;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            return this.ofCache().clear(key);
        } else {
            return this.ofCache().read(key);
        }
    }

    @Override
    public Future<T> put(final String key, final T value, final String... moreKeys) {
        if (0 == moreKeys.length) {
            return this.ofCache().write(key, value);
        } else {
            final Set<String> keySet = new HashSet<>(Arrays.asList(moreKeys));
            keySet.add(key);
            return this.ofCache().writeMulti(keySet, value);
        }
    }

    @Override
    public Future<Boolean> remove(final String... keys) {
        final List<Future<Boolean>> waitingQ = new ArrayList<>();
        Arrays.asList(keys).forEach(key -> waitingQ.add(this.ofCache().clear(key).compose(nil -> Ux.futureT())));
        return FnBase.combineB(waitingQ);
    }

    @Override
    public Rapid<String, T> ofCache() {
        return Rapid.object(this.poolName, this.configTtl());
    }
}
