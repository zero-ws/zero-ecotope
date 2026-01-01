package io.zerows.plugins.cache;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AbstractRapid<K, T> implements Rapid<K, T> {
    protected final String poolName;
    protected final int expired;
    private HPO waitPool;

    protected AbstractRapid(final String poolName, final int expired) {
        this.poolName = poolName;
        this.expired = expired;
    }


    protected AbstractRapid(final User user) {
        Objects.requireNonNull(user);
        this.expired = -1;
        final JsonObject credit = Ut.valueJObject(user.principal());
        this.poolName = credit.getString(KName.HABITUS);
    }

    protected HPO pool() {
        if (Objects.isNull(this.waitPool)) {
            this.waitPool = HPO.of(this.poolName);
        }
        return this.waitPool;
    }

    @Override
    public Future<T> write(final K key, final T value) {
        if (0 < this.expired) {
            return this.pool().put(key, value, this.expired)
                .compose(kv -> Ut.future(kv.value()));
        } else {
            return this.pool().put(key, value)
                .compose(kv -> Ut.future(kv.value()));
        }
    }

    @Override
    public Future<T> writeMulti(final Set<K> keySet, final T value) {
        final List<Future<T>> futures = new ArrayList<>();
        keySet.forEach(key -> futures.add(this.write(key, value)));
        return FnBase.combineT(futures).compose(nil -> Ut.future(value));
    }

    @Override
    public Future<Boolean> writeMulti(final Set<K> keySet) {
        final List<Future<T>> futures = new ArrayList<>();
        keySet.forEach(key -> futures.add(this.clear(key)));
        return FnBase.combineT(futures).compose(nil -> Ut.futureT());
    }

    @Override
    public Future<T> clear(final K key) {
        return this.pool().<K, T>remove(key)
            .compose(kv -> Ut.future(kv.value()));
    }

    @Override
    public Future<T> read(final K key) {
        return this.pool().get(key);
    }
}
