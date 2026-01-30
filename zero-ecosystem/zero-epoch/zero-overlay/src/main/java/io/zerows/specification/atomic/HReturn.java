package io.zerows.specification.atomic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;

/**
 * 以输入输出为主的核心高阶接口定义
 * <pre>
 *     execute -> 泛型输入
 *     executeJ -> ClusterSerializable 输入
 * </pre>
 * 根据输出拓展两个子类
 * <pre>
 *     1. 检查器：Boolean 中的 TRUE 版本
 *     2. 分流器：String 中的默认版本
 * </pre>
 *
 * @author lang : 2023-06-03
 */
public interface HReturn<I, O> {

    /**
     * （核心DTO）专用检查方法，检查数据结果是否符合预期
     *
     * @param data   数据
     * @param config 配置
     * @return {@link O}
     */
    O execute(I data, JsonObject config);

    /**
     * 「异步版本」（核心DTO）专用检查方法，检查数据结果是否符合预期
     *
     * @param data   数据
     * @param config 配置
     * @return {@link Future}
     */
    default Future<O> executeAsync(final I data, final JsonObject config) {
        return Future.succeededFuture(this.execute(data, config));
    }

    /**
     * （JsonObject）专用检查方法，检查数据结果是否符合预期
     *
     * @param data   数据
     * @param config 配置
     * @return {@link O}
     */
    O executeJ(final ClusterSerializable data, final JsonObject config);

    /**
     * 「异步版本」（JsonObject）专用检查方法，检查数据结果是否符合预期
     *
     * @param data   数据
     * @param config 配置
     * @return {@link Future}
     */
    default Future<O> executeJAsync(final ClusterSerializable data, final JsonObject config) {
        return Future.succeededFuture(this.executeJ(data, config));
    }

    /**
     * 「检查器」
     * 可以在不同的模块中使用，主要用于检查数据本身的合法性，核心使用场景：
     * <pre><code>
     *     1. 请求拦截器：可配置到 AOP 层直接拦截请求，检查请求的合法性
     *     2. 检查结果通常会以异步的方式处理
     *        - 成功：{@link Future#succeededFuture(Object)}
     *        - 失败：{@link Future#failedFuture(Throwable)}
     *     3. 不仅如此，检查的最终结果会直接返回输入信息，方便后续的处理
     * </code></pre>
     *
     * @author lang : 2023-05-27
     */
    interface HTrue<T> extends HReturn<T, Boolean> {

        @Override
        default Boolean execute(final T data, final JsonObject config) {
            return Boolean.TRUE;
        }

        @Override
        default Boolean executeJ(final ClusterSerializable data, final JsonObject config) {
            return Boolean.TRUE;
        }
    }

    /**
     * 「分流器」
     * 可以在不同的模块中使用，主要用于配置模块筛选 {@link java.lang.String} 类型的 identifier 专用，或Map配置下的 key 专用
     * <pre><code>
     *     1. AOP分流器
     *     2. 新版标识规则选择器
     * </code></pre>
     * 分流器会使用泛型T以及 JSON 格式优先的模式进行处理，主要是筛选一个键用来做配置分流行为。
     *
     * @author lang : 2023-06-03
     */
    interface HString<T> extends HReturn<T, String> {

        @Override
        default java.lang.String execute(final T data, final JsonObject config) {
            return null;
        }

        @Override
        default java.lang.String executeJ(final ClusterSerializable data, final JsonObject config) {
            return null;
        }

    }
}

