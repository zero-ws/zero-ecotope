package io.zerows.plugins.store.redis.cache;

import io.vertx.core.Future;
import io.zerows.core.database.cache.hit.AbstractL1;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class L1 extends AbstractL1 {
    private transient final L1Channel channel;

    public L1() {
        this.channel = new L1Channel();
    }

    @Override
    public <T> Future<T> readCacheAsync(final String key) {
        return this.channel.readAsync(key);
    }

    @Override
    public <T> T readCache(final String key) {
        return this.channel.read(key);
    }
}
