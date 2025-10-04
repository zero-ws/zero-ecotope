package io.zerows.corpus.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.sdk.management.OCache;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Objects;

/**
 * {@link Vertx} 实例集合
 * <pre><code>
 *     name-01 = {@link Vertx}
 *     name-02 = {@link Vertx}
 * </code></pre>
 *
 * StoreVertx 处理
 * <pre><code>
 *       StoreVertx
 *       Bundle-01 -> StoreVertx ->                 name-x = Vertx
 *       Bundle-02 -> StoreVertx ->                 name-y = Vertx
 *       Bundle-03 -> StoreVertx ->                 name-z = Vertx
 * </code></pre>
 *
 * @author lang : 2024-04-30
 */
public interface StoreVertx extends OCache<RunVertx> {
    // 工厂部分
    Cc<String, StoreVertx> CC_SKELETON = Cc.open();

    static StoreVertx of() {
        return of(null);
    }

    static StoreVertx of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, StoreVertxAmbiguity.class);
        return CC_SKELETON.pick(() -> new StoreVertxAmbiguity(bundle), cacheKey);
    }

    static StoreVertx ofOr(final Class<?> clazz) {
        final HBundle bundle = HPI.findBundle(clazz);
        return of(bundle);
    }

    // 内置对象
    Vertx vertx(String name);

    Vertx vertx();

    // --------------------- 基本操作 ---------------------
    @Override
    default StoreVertx remove(final RunVertx runVertx) {
        Objects.requireNonNull(runVertx);
        return this.remove(runVertx.name());
    }

    StoreVertx remove(String name);

    RunVertx valueGet(int hashCode);
}
