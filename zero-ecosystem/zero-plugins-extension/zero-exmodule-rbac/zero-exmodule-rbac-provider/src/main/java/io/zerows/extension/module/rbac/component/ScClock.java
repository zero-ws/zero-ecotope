package io.zerows.extension.module.rbac.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.extension.module.rbac.metadata.ScToken;
import io.zerows.support.Ut;

/**
 * 操作
 * <pre><code>
 *     1. 生成对应的值，两种生成方式
 *        - 带配置生成
 *        - 不带配置生成
 *     2. 存储对应的值
 *     3. 获取对应的值
 *     4. 强制删除
 * </code></pre>
 * 操作对象
 * <pre><code>
 *     1. 授权码
 *     2. 短信码
 *     3. 令牌
 *     4. 图片验证码 Captcha
 * </code></pre>
 * 没有统一归口的部分：用户、组、角色、ACL管理部分，此处的 {T} 类型就是当前每种特殊组件处理的类型。
 *
 * @param <T> 当前操作的核心生成类型，此类型现阶段包括 {@link ScToken} 和 {@link String} 两种类型
 *
 * @author lang : 2024-09-14
 */
public interface ScClock<T> {

    Cc<String, ScClock<?>> CC_SKELETON = Cc.open();

    /**
     * 不带配置的生成函数，可直接生成对应的信息
     *
     * @return 生成结果
     */
    default T generate() {
        return this.generate(null);
    }

    /**
     * 带配置的生成函数，配置中的包含了生成所需的基础数据信息
     *
     * @param config 配置/数据信息
     *
     * @return 生成结果
     */
    T generate(JsonObject config);


    /**
     * 仅读取，不删除，标准方式（只要在时间范围内一定可以读取到）
     * <pre><code>
     *     1. 非一次性读取，根据后台设置的 TTL 来判断当前内容是否应该删除，若超时则删除，否则保留。
     *     2. 一次性读取，当时读取之后就直接从缓存中移除，不再保留。
     * </code></pre>
     *
     * @param key    键
     * @param isOnce 是否一次性读取
     *
     * @return 读取结果（异步）
     */
    Future<T> get(String key, boolean isOnce);

    Future<Boolean> remove(String... key);

    /**
     * 多维度写缓存操作，支持多键写入
     * <pre><code>
     *     1. 键值对使用：key = findRunning 的方式
     *     2. 额外的键会是一个数组：moreKeys = [key1, key2, ...]
     *        最终构造的数据结构如：
     *        key   = findRunning
     *        key1  = findRunning
     *        key2  = findRunning
     *        ...   = findRunning
     *     3. 内置的 ttl 信息参考方法
     * </code></pre>
     *
     * @param key      写入缓存的主要键
     * @param value    写入缓存的值
     * @param moreKeys 额外的键信息
     *
     * @return 写入结果（异步）
     */
    Future<T> put(String key, T value, String... moreKeys);

    /**
     * 验证专用方法，用于验证当前存储值和等待验证值是否相同，内置会抛出对应的 {@see WebException} 异常
     *
     * @param stored   缓存中存储的值
     * @param waiting  等待验证的值（字面量）
     * @param identity 验证标识
     *
     * @return 验证结果（异步）
     */
    <R> Future<R> verify(T stored, String waiting, String identity);

    /**
     * 返回缓存的实例信息，设置 TTL，数值单位为秒
     *
     * @return TTL（秒）
     */
    int configTtl();

    Rapid<String, T> ofCache();

    default LogO logger() {
        return Ut.Log.security(this.getClass());
    }
}
