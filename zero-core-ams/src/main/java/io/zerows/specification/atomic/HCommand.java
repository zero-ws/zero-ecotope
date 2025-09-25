package io.zerows.specification.atomic;

import io.zerows.core.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 「抽象指令」
 * <hr/>
 * 指令抽象到底层某个单独行为中（底层封装），指令主要包含单个核心方法用于处理原子级行为：
 * <pre><code>
 *     1. configure：配置、初始化
 *     2. compile：编译、验证、实例化
 *     3. synchro：更新、同步、变化
 * </code></pre>
 * 三个抽象指令可用在任意场景。
 *
 * @param <I> 输入参数类型
 * @param <R> 返回值类型
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HCommand<I, R> {
    /**
     * 步骤1：初始化/配置
     *
     * @param input 输入参数
     *
     * @return 返回值
     */
    R configure(I input);

    /**
     * 步骤2：一致性保持最新，同步
     *
     * @param input   输入参数
     * @param request 原始请求：原始请求用于中间 Monad 捕捉最初输入专用
     *
     * @return 返回值
     */
    default R synchro(final I input, final JsonObject request) {
        return null;
    }


    /**
     * 步骤3：验证后期处理环节
     *
     * @param input   输入参数
     * @param request 原始请求：原始请求用于中间 Monad 捕捉最初输入专用
     *
     * @return 返回值
     */
    default R compile(final I input, final JsonObject request) {
        return null;
    }

    /**
     * 「异步指令」
     * <hr/>
     * 系统指令的异步版本，该指令主要用于异步执行，异步执行的指令会返回 {@link Future} 类型的结果。
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface Async<I, R> extends HCommand<I, Future<R>> {

        /**
         * 绑定 Vertx 实例
         *
         * @param vertx {@link Vertx} 实例
         * @param <T>   泛型
         *
         * @return {@link T} 绑定后的实例
         */
        @SuppressWarnings("unchecked")
        default <T> T bind(final Vertx vertx) {
            return (T) this;
        }

        /**
         * 步骤1：初始化/配置
         *
         * @param input 输入参数
         *
         * @return 返回值
         */
        @Override
        default Future<R> configure(final I input) {
            return Future.failedFuture(new _501NotSupportException(this.getClass()));
        }

        /**
         * 步骤2：一致性保持最新，同步
         *
         * @param input   输入参数
         * @param request 原始请求：原始请求用于中间 Monad 捕捉最初输入专用
         *
         * @return 返回值
         */
        @Override
        default Future<R> synchro(final I input, final JsonObject request) {
            return Future.failedFuture(new _501NotSupportException(this.getClass()));
        }

        /**
         * 步骤3：验证后期处理环节
         *
         * @param input   输入参数
         * @param request 原始请求：原始请求用于中间 Monad 捕捉最初输入专用
         *
         * @return 返回值
         */
        @Override
        default Future<R> compile(final I input, final JsonObject request) {
            return Future.failedFuture(new _501NotSupportException(this.getClass()));
        }
    }
}
