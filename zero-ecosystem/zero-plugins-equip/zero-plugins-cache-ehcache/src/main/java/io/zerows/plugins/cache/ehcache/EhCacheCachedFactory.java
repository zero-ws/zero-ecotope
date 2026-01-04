package io.zerows.plugins.cache.ehcache;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.cache.CachedFactory;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@SPID
public class EhCacheCachedFactory implements CachedFactory {
    /**
     * 最特殊的一个 MemoAt 实现，通过 Ehcache 进行数据缓存，它的特殊性在于此处不能使用 key = value 进行缓存，每次都直接
     * 创建一个全新的 Cache 缓存实例，原因在于此处的 K,V 类型是不确定的，必须通过配置项进行强制转换，否则会出现类型转换异常，
     * 加上 Ehcache 本身的设计依赖 K, V 构造，这是 Ehcache 的设计缺陷！
     *
     * @param vertx   Vertx 容器
     * @param options 缓存配置
     * @param <K>     键类型
     * @param <V>     值类型
     * @return EhCacheMemoAt 实例
     */
    @Override
    public <K, V> MemoAt<K, V> findConfigured(final Vertx vertx, final MemoOptions<K, V> options) {
        final EhCacheYmConfig config = this.configOf(options);
        if (Objects.isNull(config)) {
            log.warn("[ R2MO ] 配置缺失，无法构造此类 MemoAt，请检查：{}", options.extension());
            return null;
        }

        // 构造新的 MemoOptions，此处 fingerprint 会有变化
        final Duration expiredAt = config.expiredAt();
        final MemoOptions<K, V> optionsUpdated = options.of(expiredAt);
        optionsUpdated.size(config.getSize());
        optionsUpdated.configuration(config);
        return this.findBy(vertx, optionsUpdated);
    }

    @Override
    public <K, V> MemoAt<K, V> findBy(final Vertx vertx, final MemoOptions<K, V> options) {
        Objects.requireNonNull(options, "[ R2MO ] MemoOptions 不能为空！");
        return new EhCacheMemoAt<>(vertx, options);
    }

    private <K, V> EhCacheYmConfig configOf(final MemoOptions<K, V> options) {
        Objects.requireNonNull(options);
        final JsonObject extension = options.extension();
        if (Objects.isNull(extension)) {
            return null;
        }
        final JsonObject optionJ = Ut.valueJObject(extension, "ehcache");
        return Ut.deserialize(optionJ, EhCacheYmConfig.class);
    }
}
