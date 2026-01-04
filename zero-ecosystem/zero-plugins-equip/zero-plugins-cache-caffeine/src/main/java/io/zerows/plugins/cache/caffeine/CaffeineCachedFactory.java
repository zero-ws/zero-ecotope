package io.zerows.plugins.cache.caffeine;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.cache.CachedFactory;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@SPID(priority = 50)
@Slf4j
public class CaffeineCachedFactory implements CachedFactory {

    private static final Cc<String, MemoAt<?, ?>> CC_MEMO = Cc.openThread();

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> MemoAt<K, V> findMemoAt(final Vertx vertx, final MemoOptions<K, V> options) {
        final CaffeineYmConfig config = this.configOf(options);
        if (Objects.isNull(config)) {
            log.warn("[ R2MO ] 配置缺失，无法构造此类 MemoAt，请检查：{}", options.extension());
            return null;
        }


        // 构造新的 MemoOptions，此处 fingerprint 会有变化
        final MemoOptions<K, V> optionsUpdated = options.of(config.expiredAt());
        optionsUpdated.size(config.getInitialCapacity());
        optionsUpdated.configuration(config);

        final String fingerprint = optionsUpdated.fingerprint();
        return (MemoAt<K, V>) CC_MEMO.pick(() -> new CaffeineMemoAt<>(vertx, optionsUpdated), fingerprint);
    }

    private <K, V> CaffeineYmConfig configOf(final MemoOptions<K, V> options) {
        Objects.requireNonNull(options);
        final JsonObject extension = options.extension();
        if (Objects.isNull(extension)) {
            return null;
        }
        final JsonObject optionJ = Ut.valueJObject(extension, "caffeine");
        return Ut.deserialize(optionJ, CaffeineYmConfig.class);
    }
}
