package io.zerows.plugins.cache;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Deprecated
@Slf4j
public class RapidUser<T> extends AbstractRapid<String, T> {
    private final transient String rootKey;

    RapidUser(final User user, final String rootKey) {
        super(user);
        this.rootKey = rootKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<T> cached(final String key, final Supplier<Future<T>> executor) {
        Objects.requireNonNull(key);
        return this.pool().<String, JsonObject>get(this.rootKey).compose(cached -> {
            if (Objects.isNull(cached)) {
                cached = new JsonObject();
            }
            if (cached.containsKey(key)) {
                log.info("[ PLUG ] ( Pool ) \u001b[0;37mK = `{}`, R = `{}`, P = `{}`\u001b[m",
                        key, this.rootKey, this.pool().name());
                return Ut.future((T) cached.getValue(key));
            }


            final JsonObject stored = cached;
            return executor.get().compose(item -> {
                stored.put(key, item);
                return this.pool().put(this.rootKey, stored).compose(nil -> Ut.future(item));
            });
        });
    }
}
