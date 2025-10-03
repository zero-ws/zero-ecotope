package io.zerows.component.destine;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.Memory;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.metadata.specification.KJoin;

/**
 * 针对组件 {@link Conflate} 的专用内部构造接口，此接口可根据不同输入信息构造对应的条件
 * 形成缓存专用的 key，解决 501 的内部问题，问题描述如下：
 * <pre><code>
 *     [ ERR-60050 ] ( ConflateJQr ) Web Error: (501) -
 *         This api is not supported or implemented, please check your specification
 * </code></pre>
 *
 * @author lang : 2023-08-10
 */
@SuppressWarnings("all")
interface BuilderConflate {
    @Memory(Conflate.class)
    Cc<String, Conflate> CCT_CONFLATE = Cc.openThread();

    /**
     * 查询条件构造器专用静态方法，此方法会关联两个核心实现类
     * <pre><code>
     *     {@link ConflateAQr}
     *     {@link ConflateJQr}
     * </code></pre>
     *
     * @param joinRef {@link KJoin} 组件引用
     * @param isArray 是否为数组
     * @param <I>     输入类型
     *
     * @return {@link Conflate} 实例
     */
    static <I> Conflate<I, JsonObject> ofQr(final KJoin joinRef, final boolean isArray) {
        if (isArray) {
            final String cacheKey = String.valueOf(joinRef.hashCode()) + VString.SLASH +
                ConflateAQr.class.getName();
            return CCT_CONFLATE.pick(() -> new ConflateAQr(joinRef), cacheKey);
        } else {
            final String cacheKey = String.valueOf(joinRef.hashCode()) + VString.SLASH +
                ConflateJQr.class.getName();
            return CCT_CONFLATE.pick(() -> new ConflateJQr(joinRef), cacheKey);
        }
    }

    /**
     * 批量专用处理模式，此模式用于处理两个方向的实现
     * <pre><code>
     *     1. 输入：{@link ConflateAIo} + false
     *     2. 输出：{@link ConflateAIo} + true
     * </code></pre>
     *
     * @param joinRef {@link KJoin} 组件引用
     * @param isOut   方向是输出
     *
     * @return {@link Conflate} 实例
     */
    static Conflate<JsonArray, JsonArray> ofJArray(final KJoin joinRef, final boolean isOut) {
        final String cacheKey = String.valueOf(joinRef.hashCode()) + VString.SLASH +
            ConflateAIo.class.getName() + VString.SLASH + String.valueOf(isOut);
        if (isOut) {
            return CCT_CONFLATE.pick(() -> new ConflateAIo(joinRef, true), cacheKey);
        } else {
            return CCT_CONFLATE.pick(() -> new ConflateAIo(joinRef, false), cacheKey);
        }
    }

    /**
     * 单量专用处理模式，此模式用于处理的两个实现类
     * <pre><code>
     *     1. 输入：{@link ConflateJIn}
     *     2. 输出：{@link ConflateJOut}
     * </code></pre>
     *
     * @param joinRef {@link KJoin} 组件引用
     * @param isOut   方向是输出
     *
     * @return {@link Conflate} 实例
     */
    static Conflate<JsonObject, JsonObject> ofJObject(final KJoin joinRef, final boolean isOut) {
        if (isOut) {
            final String cacheKey = String.valueOf(joinRef.hashCode()) + VString.SLASH +
                ConflateJOut.class.getName();
            return CCT_CONFLATE.pick(() -> new ConflateJOut(joinRef), cacheKey);
        } else {
            final String cacheKey = String.valueOf(joinRef.hashCode()) + VString.SLASH +
                ConflateJIn.class.getName();
            return CCT_CONFLATE.pick(() -> new ConflateJIn(joinRef), cacheKey);
        }
    }
}
