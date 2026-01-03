package io.zerows.plugins.cache;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.epoch.constant.KName;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Defer
@Slf4j
class SharedClientImpl implements SharedClient {

    private static final Cc<String, SharedClient> CC_CLIENTS = Cc.openThread();

    private final transient Vertx vertx;
    private final MemoOptions<?, ?> baseOption;

    @SuppressWarnings("all")
    private SharedClientImpl(final Vertx vertx, final JsonObject options) {
        this.vertx = vertx;
        String poolName = Ut.valueString(options, KName.NAME);

        final Class<?> caller = this.vertx.isClustered() ? MemoAtMapCluster.class : MemoAtMapLocal.class;
        final Class classK = SourceReflect.clazz(Ut.valueString(options, "classK"));
        final Class classV = SourceReflect.clazz(Ut.valueString(options, "classV"));
        final Integer size = Ut.valueInt(options, KName.SIZE, 0);
        this.baseOption = new MemoOptions<>(caller)
                .name(poolName)
                .classK(classK).classV(classV)
                .size(size);
    }

    static SharedClient create(final Vertx vertx, final JsonObject options) {
        final String name = Ut.valueString(options, KName.NAME);
        final String cacheKey = vertx.hashCode() + "@" + name;
        return CC_CLIENTS.pick(() -> new SharedClientImpl(vertx, options), cacheKey);
    }

    /**
     * 数量设置
     * <pre>
     *     {@link SharedManager} x 1
     *     {@link SharedAddOn}   x 1
     *     {@link SharedClient}  x N  --> name
     *                                    默认：name = {@link AddOn.Name}
     *                                    其他：name = {@link SharedAddOn#createInstance} 参数直接指定
     *     {@link MemoAt}        x N  --> 键：vertx + options 指纹作为核心管理键，但是如果带有 ttl 会创建新的
     * </pre>
     *
     * @param expiredAt 过期时间
     * @param <K>       键类型
     * @param <V>       值类型
     * @return MemoAt 实例
     */
    @Override
    public <K, V> MemoAt<K, V> memoAt(final Duration expiredAt) {
        /*
         * 此处对应 SharedClient 下的 MemoAt 实例创建逻辑
         */
        final MemoOptions<K, V> optionsWithTTL = this.baseOption.of(expiredAt);
        return BaseClient.of(this.vertx, optionsWithTTL);
    }
}
