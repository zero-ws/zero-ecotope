package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.token.TransferTokenPool;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Zero 侧基于 HMM 的 TransferTokenPool。
 * HMM 底层可走 Redis/Caffeine/EhCache，具体由 Zero 缓存 SPI 决定。
 */
@Slf4j
class HmmTokenPool implements TransferTokenPool {

    private static final String CACHE_NAME = "ambient.upload.transfer.token";
    private static final long DEFAULT_EXPIRE_MS = 60 * 60 * 1000L;

    private final HMM<String, TransferToken> cache = HMM.of(CACHE_NAME);

    @Override
    public boolean runSave(final TransferToken token, final long expiredAt) {
        if (Objects.isNull(token) || Ut.isNil(token.getToken())) {
            return false;
        }
        final long ttlMs = this.ttlMs(expiredAt);
        token.setExpiredAt(LocalDateTime.now().plusNanos(ttlMs * 1_000_000));
        return Objects.nonNull(this.await(this.cache.put(token.getToken(), token, Duration.ofMillis(ttlMs))));
    }

    @Override
    public boolean runExtend(final String token, final long expiredAt) {
        final TransferToken found = this.findBy(token);
        if (Objects.isNull(found)) {
            return false;
        }
        return this.runSave(found, expiredAt);
    }

    @Override
    public boolean runDelete(final String token) {
        if (Ut.isNil(token)) {
            return false;
        }
        return null != this.await(this.cache.remove(token));
    }

    @Override
    public int runClean(final boolean expiredOnly) {
        if (expiredOnly) {
            int cleaned = 0;
            final Set<String> keys = this.await(this.cache.keySet());
            if (Objects.isNull(keys)) {
                return 0;
            }
            for (final String key : keys) {
                final TransferToken found = this.findBy(key);
                if (Objects.isNull(found)) {
                    cleaned++;
                }
            }
            return cleaned;
        }
        final Integer size = this.await(this.cache.size());
        this.await(this.cache.clear());
        return Objects.isNull(size) ? 0 : size;
    }

    @Override
    public TransferToken findBy(final String token) {
        if (Ut.isNil(token)) {
            return null;
        }
        final TransferToken found = this.await(this.cache.find(token));
        if (Objects.isNull(found)) {
            return null;
        }
        if (Objects.nonNull(found.getExpiredAt()) && found.getExpiredAt().isBefore(LocalDateTime.now())) {
            this.runDelete(token);
            return null;
        }
        return found;
    }

    @Override
    public boolean isExists(final String token) {
        return Objects.nonNull(this.findBy(token));
    }

    @Override
    public long getExpired(final String token) {
        final TransferToken found = this.findBy(token);
        if (Objects.isNull(found) || Objects.isNull(found.getExpiredAt())) {
            return -1L;
        }
        final long ms = Duration.between(LocalDateTime.now(), found.getExpiredAt()).toMillis();
        return Math.max(ms, -1L);
    }

    private long ttlMs(final long expiredAt) {
        if (expiredAt <= 0) {
            return DEFAULT_EXPIRE_MS;
        }
        final long ttl = expiredAt - System.currentTimeMillis();
        return ttl > 0 ? ttl : DEFAULT_EXPIRE_MS;
    }

    private <T> T await(final io.vertx.core.Future<T> future) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> resultRef = new AtomicReference<>();
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                resultRef.set(ar.result());
            } else {
                errorRef.set(ar.cause());
            }
            latch.countDown();
        });
        try {
            if (!latch.await(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("[ XMOD ] HMM token pool 等待超时");
            }
            if (Objects.nonNull(errorRef.get())) {
                throw new RuntimeException(errorRef.get());
            }
            return resultRef.get();
        } catch (final Exception ex) {
            log.error("[ XMOD ] HMM token pool 异步等待失败", ex);
            return null;
        }
    }
}
