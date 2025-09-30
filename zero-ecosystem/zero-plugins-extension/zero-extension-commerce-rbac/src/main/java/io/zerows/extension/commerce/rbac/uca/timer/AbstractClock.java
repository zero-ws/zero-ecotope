package io.zerows.extension.commerce.rbac.uca.timer;

import io.vertx.core.Future;
import io.zerows.core.fn.FnZero;
import io.zerows.core.web.cache.Rapid;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import io.zerows.unity.Ux;
import org.osgi.framework.Bundle;

import java.util.*;

/**
 * @author lang : 2024-09-14
 */
abstract class AbstractClock<T> extends AbstractAmbiguity implements ScClock<T> {

    private final String poolName;

    AbstractClock(final Bundle bundle, final String poolName) {
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
        return FnZero.combineB(waitingQ);
    }

    @Override
    public Rapid<String, T> ofCache() {
        return Rapid.object(this.poolName, this.configTtl());
    }
}
