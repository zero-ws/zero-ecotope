package io.zerows.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.cache.MemoAt;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;
import io.zerows.sdk.plugins.AddOn;

import java.time.Duration;

/**
 * @author lang : 2026-01-02
 */
class CachedClientImpl implements CachedClient {

    private static final Cc<String, CachedClient> CC_CLIENTS = Cc.openThread();

    private final transient Vertx vertx;
    private final transient String poolName;

    private CachedClientImpl(final Vertx vertx, final String name) {
        this.vertx = vertx;
        this.poolName = name;
    }

    static CachedClient create(final Vertx vertx, final String name) {
        final String cacheKey = vertx.hashCode() + "@" + name;
        return CC_CLIENTS.pick(() -> new CachedClientImpl(vertx, name), cacheKey);
    }

    /**
     * 数量设置
     * <pre>
     *     {@link CachedManager} x 1
     *     {@link CachedAddOn}   x 1
     *     {@link CachedClient}  x N  --> name
     *                                    默认：name = {@link AddOn.Name}
     *                                    其他：name = {@link CachedAddOn#createInstanceBy} 参数直接指定
     *     {@link MemoAt}        x N  --> 键：vertx + options 指纹作为核心管理键，但是如果带有 ttl 会创建新的
     * </pre>
     * 此方法流程的区别：{@link SharedClientImpl} 实现中创建内容是 Vertx 原生的两种，{@link AsyncMap} 和 {@link LocalMap}，而此处
     * 则是基于第三方缓存实现，所以主要依赖实现层，所以要走特殊的 SPI 进行创建。不直接提取的原因：EhCache 的实现原理间接影响了实现流程，限制
     * 如：EhCache 不能仅依靠 （调用者、配置项目类型、缓存名、键类型 / 值类型）的维度来创建缓存实例，最终会诱发如下两种分流
     * <pre>
     *     1. EhCache / 全属性指纹 ---
     *        @ / 缓存本身依靠 name 构造 Manager，然后依靠 name / keyClass / valueClass / size / expiredAt 五个维度来构造缓存实例
     *        - caller 调用者
     *        - 配置项目类型
     *        - 缓存名称
     *        - 键类型 / 值类型
     *        - 尺寸
     *        - 过期时间
     *     2. 其他缓存实现 / 标准指纹 ---
     *        @ / 缓存仅依靠 name 即可
     *        - caller 调用者
     *        - 配置项目类型
     *        - 缓存名称
     *        - 键类型 / 值类型（可选）
     * </pre>
     * 不论哪种模式，在生成指纹时过期时间和尺寸都不参与计算，此处指纹运算主要 {@link MemoAt} 组件的指纹，并非底层缓存指纹。
     *
     * @param expiredAt 过期时间
     * @param <K>       键类型
     * @param <V>       值类型
     * @return MemoAt 实例
     */
    @Override
    public <K, V> MemoAt<K, V> memoAt(final Duration expiredAt) {
        return null;
    }
}
