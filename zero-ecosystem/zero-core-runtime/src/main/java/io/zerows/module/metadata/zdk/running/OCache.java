package io.zerows.module.metadata.zdk.running;

import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存接口，用来存储全局缓存数据，内置缓存数据结构主要有两种
 * <pre><code>
 *     1. Set：集合
 *     2. Map：哈希表
 * </code></pre>
 * 还有一种比较特殊的是内置
 * <pre><code>
 *     Map : key1 = Set1
 *           key2 = Set2
 *           ...
 * </code></pre>
 *
 * @author lang : 2024-04-17
 */
public interface OCache<T> {
    /**
     * 全局注册表，构造的时候就注册进来，由 {@link AbstractAmbiguity} 负责注册，可枚举
     * Bundle 内的 Cache 列表，如此可知道 Cache 的运行情况
     */
    ConcurrentMap<String, Set<Class<?>>> REGISTRY = new ConcurrentHashMap<>();

    // ------------ 初始化 ---------------

    /**
     * 选择性配置，可以直接根据传入配置类型执行初始化行为，此配置仅用于十分特殊的场景，当前缓存无法满足基本需要时执行此接口方法操作，可执行
     * 配置相关的对接行为，使得缓存本身可执行使用之前的初始化，延迟加载部分性能损耗比较明显的场景。
     *
     * @param configuration 配置对象
     * @param <C>           配置类型
     *
     * @return {@link OCache} 实例
     */
    default <C> OCache<T> configure(final C configuration) {
        return this;
    }

    // ------------ 数据提取接口 ---------------

    /**
     * 只返回当前 {@link Bundle} 中存储的键值数据
     * <pre><code>
     *     1. 如果是 Set 则返回每一个元素的
     *        1）有 id 返回 id（key）
     *        2）没有 id 则返回 Class Name
     *     2. 如果是 Map / JsonObject 则直接返回 keySet
     * </code></pre>
     *
     * @return 返回当前缓存中的所有键
     */
    default Set<String> keys() {
        throw Ut.Bnd.failWeb(_501NotSupportException.class, this.getClass());
    }

    /**
     * 一般情况下 T 是一个集合，此处的返回值通常会衍生到 Set 的叠加操作
     *
     * @return 返回当前缓存中的所有值
     */
    default T value() {
        throw Ut.Bnd.failWeb(_501NotSupportException.class, this.getClass());
    }

    /**
     * 这种方法通常仅用于 Hash 表
     *
     * @param key 键值
     *
     * @return 需筛选的类型
     */
    default T valueGet(final String key) {
        throw Ut.Bnd.failWeb(_501NotSupportException.class, this.getClass());
    }

    // -------------------- 两种模式的追加和移除 --------------------
    OCache<T> add(T t);

    OCache<T> remove(T t);

    default OLog logger() {
        return Ut.Log.cache(this.getClass());
    }
}
