package io.zerows.plugins.store.redis.cache;

import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.plugins.store.redis.RedisInfix;
import redis.clients.jedis.Jedis;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class L1ChannelSync {
    private final static Annal LOGGER = Annal.get(L1ChannelSync.class);
    private final transient Jedis jedis;

    L1ChannelSync() {
        this.jedis = RedisInfix.getJClient();
    }

    @SuppressWarnings("all")
    <T> T read(final String key) {
        /*
         * Async convert to type
         */
        if (Objects.isNull(this.jedis)) {
            // Nothing returned
            return null;
        } else {
            final String literal = this.jedis.get(key);
            if (Ut.isNil(literal)) {
                LOGGER.info(CacheMsg.HIT_FAILURE, key);
                return null;
            } else {
                if (Ut.isJObject(literal)) {
                    /*
                     * Data Found
                     */
                    LOGGER.info(CacheMsg.HIT_DATA, key);
                    return (T) Ut.toJObject(literal);
                } else if (Ut.isJArray(literal)) {
                    LOGGER.info(CacheMsg.HIT_DATA, key);
                    return (T) Ut.toJArray(literal);
                } else {
                    /*
                     * Secondary read
                     */
                    LOGGER.info(CacheMsg.HIT_SECONDARY, literal, key);
                    return this.read(literal);
                }
            }
        }
    }
}
