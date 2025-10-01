package io.zerows.epoch.support;

import io.r2mo.typed.exception.AbstractException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * HFn采用了Java中的静态原型链结构
 *
 * @author lang : 2023/4/27
 */
public class FnBase {
    protected FnBase() {
    }

    public static <T> Function<Throwable, T> outAsync(final Supplier<T> supplier) {
        return FnVertx.otherwiseFn(supplier);
    }

    /**
     * ✅ 并行检查器，检查所有的异步结果，全部为 true 时则通过检查，
     * 🎯 最终返回双态 Monad
     *
     * @param response  📤 响应
     * @param error     ⚠️ 检查不通过抛出的异常
     * @param executors ⚙️ 执行器
     * @param <T>       💾 响应类型
     * @param <E>       🚨 异常类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T, E extends AbstractException> Future<T> passAll(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnVertx.passAll(response, error, executors);
    }

    /**
     * 🔍 并行检查器，检查所有的异步结果，只要有一个为 true 时则通过检查，
     * 🎯 最终返回双态 Monad
     *
     * @param response  📤 响应
     * @param error     ⚠️ 检查不通过抛出的异常
     * @param executors ⚙️ 执行器
     * @param <T>       💾 响应类型
     * @param <E>       🚨 异常类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T, E extends AbstractException> Future<T> passAny(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnVertx.passAny(response, error, executors);
    }

    /**
     * 🚫 并行检查器，检查所有的异步结果，所有结果都为 false 时则通过检查，
     * 🎯 当且仅当所有检查都返回 false 时才通过
     *
     * @param response  📤 响应
     * @param error     ⚠️ 检查不通过抛出的异常
     * @param executors ⚙️ 执行器
     * @param <T>       💾 响应类型
     * @param <E>       🚨 异常类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T, E extends AbstractException> Future<T> passNone(
        final T response, final E error,
        final Set<Function<T, Future<Boolean>>> executors) {
        return FnVertx.passNone(response, error, executors);
    }

    /**
     * ⚡ 并行编排器，此种编排器不在意执行结果，只关心执行是否成功，工作流程如下：
     * <pre><code>
     *     📥 input -> executor1 -> output1 -> Future<input>
     *               -> executor2 -> output2
     *               -> executor3 -> output3
     *               ...
     *               -> executorN -> outputN
     * </code></pre>
     *
     * 🔄 整体流程如，其中所有的 executor 是同时执行
     * <pre><code>
     *     📥 input -> executor1
     *                executor2
     *                executor3 -> 📤 output
     * </code></pre>
     *
     * @param input     📥 输入
     * @param executors ⚙️ 执行器
     * @param <T>       💾 输入类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T> Future<T> parallel(final T input, final Set<Function<T, Future<T>>> executors) {
        return FnVertx.parallel(input, executors);
    }

    /**
     * 🔄 并行编排器
     * 🎯 提供 List 接口的便捷调用
     *
     * @param input     📥 输入
     * @param executors ⚙️ 执行器
     * @param <T>       💾 输入类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T> Future<T> parallel(final T input, final List<Function<T, Future<T>>> executors) {
        return FnVertx.parallel(input, new HashSet<>(executors));
    }

    /**
     * 🛠️ 并行编排器
     * 🎯 提供可变参数的便捷调用
     *
     * @param input     📥 输入
     * @param executors ⚙️ 执行器
     * @param <T>       💾 输入类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    @SafeVarargs
    public static <T> Future<T> parallel(final T input, final Function<T, Future<T>>... executors) {
        return FnVertx.parallel(input, new HashSet<>(Arrays.asList(executors)));
    }

    /**
     * 🎯 异步串行编排器，工作流程如下：
     * <pre><code>
     *     📥 input -> executor1 -> output1 ->
     *                executor2 -> output2 ->
     *                executor3 -> output3 ->
     *                ...
     *                executorN -> outputN -> Future<outputN>
     * </code></pre>
     * 🔄 执行流程过程中每一个步骤的输出结果会作为下一个执行的输入，整体流程如：
     * <pre><code>
     *     📥 input -> executor1 -> executor2 -> executor3 -> executorN
     * </code></pre>
     *
     * @param input     📥 输入
     * @param executors ⚙️ 执行器
     * @param <T>       💾 输入类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    public static <T> Future<T> passion(final T input, final List<Function<T, Future<T>>> executors) {
        return FnVertx.passion(input, executors);
    }

    /**
     * 🔗 异步串行编排器
     * 🎯 提供可变参数的便捷调用
     *
     * @param input     📥 输入
     * @param executors ⚙️ 执行器
     * @param <T>       💾 输入类型
     *
     * @return {@link Future} 🌟 异步结果
     */
    @SafeVarargs
    public static <T> Future<T> passion(final T input, final Function<T, Future<T>>... executors) {
        return FnVertx.passion(input, Arrays.asList(executors));
    }

    /**
     * 🔄 二元组合函数 - Future 合并模式
     *
     * @param futureF    📤 Future<F> 输入的异步结果，结果内是 F
     * @param futureS    📤 Future<S> 输入的异步结果，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(
        final Future<F> futureF, final Future<S> futureS,
        final BiFunction<F, S, Future<T>> combinerOf) {
        return FnVertx.combineT(() -> futureF, () -> futureS, combinerOf);
    }

    /**
     * 🔗 二元组合函数的顺序模式
     *
     * @param supplierF  🔧 Supplier<Future<F>> 输入的异步结果，结果内是 F
     * @param functionS  🔧 Function<F, Future<S>> 输入的异步结果，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Supplier<Future<F>> supplierF,
                                               final Function<F, Future<S>> functionS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnVertx.combineT(supplierF, functionS, combinerOf);
    }

    /**
     * 🔗 二元组合函数 - Future + Function 模式
     *
     * @param futureF    📤 Future<F> 预执行的异步结果
     * @param functionS  🔧 Function<F, Future<S>> 依赖第一个结果的异步函数
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Future<F> futureF,
                                               final Function<F, Future<S>> functionS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnVertx.combineT(() -> futureF, functionS, combinerOf);
    }

    /**
     * 📦 二阶组合函数 - 集合处理模式
     *
     * @param futureL    📤 Future<List<S>> 输入的异步结果，结果内是 List<S>
     * @param combinerOf 🔧 Function<S, Future<T>> 组合函数，输入为 S，输出为 Future<T>
     * @param <I>        💾 输入集合元素类型 I
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <I, T> Future<List<T>> combineT(final Future<List<I>> futureL, final Function<I, Future<T>> combinerOf) {
        return futureL.compose(source -> combineT(source, combinerOf));
    }

    /**
     * 🔄 组合函数最简单的模式 - List 版本
     *
     * @param futures List<Future<T>> 📤 输入的异步结果，结果内是 T
     * @param <T>     💾 泛型类型
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <T> Future<List<T>> combineT(final List<Future<T>> futures) {
        return FnVertx.combineT(futures);
    }

    /**
     * 🔄 组合函数最简单的模式 - Set 版本
     *
     * @param futures Set<Future<T>> 📤 输入的异步结果，结果内是 T
     * @param <T>     💾 泛型类型
     *
     * @return Future<Set<T>> 🌟 返回执行过的结果集合
     */
    public static <T> Future<Set<T>> combineT(final Set<Future<T>> futures) {
        return FnVertx.combineT(futures);
    }

    /**
     * 🔄 组合函数的同步模式 - List 版本
     *
     * @param source     📥 输入的集合 List<I>
     * @param combinerOf 🔧 Function<I, Future<T>> 组合函数，输入为 I，输出为 Future<T>
     * @param <I>        💾 输入类型I
     * @param <T>        🎯 输出类型T
     *
     * @return Future<List<T>> 🌟 返回执行过的结果数组
     */
    public static <I, T> Future<List<T>> combineT(final List<I> source,
                                                  final Function<I, Future<T>> combinerOf) {
        final List<Future<T>> futures = new ArrayList<>();
        source.stream().map(combinerOf).forEach(futures::add);
        return FnVertx.combineT(futures);
    }

    /**
     * 🔄 组合函数的同步模式 - Set 版本
     *
     * @param source     📥 输入的集合 Set<I>
     * @param combinerOf 🔧 Function<I, Future<T>> 组合函数，输入为 I，输出为 Future<T>
     * @param <I>        💾 输入类型I
     * @param <T>        🎯 输出类型T
     *
     * @return Future<Set<T>> 🌟 返回执行过的结果集合
     */
    public static <I, T> Future<Set<T>> combineT(final Set<I> source,
                                                 final Function<I, Future<T>> combinerOf) {
        final Set<Future<T>> futures = new HashSet<>();
        source.stream().map(combinerOf).forEach(futures::add);
        return FnVertx.combineT(futures);
    }

    /**
     * 🔄 二元组合函数 - 延迟执行模式
     *
     * @param supplierF  🔧 Supplier<Future<F>> 输入的异步结果执行函数，结果内是 F
     * @param supplierS  🔧 Supplier<Future<S>> 输入的异步结果执行函数，结果内是 S
     * @param combinerOf 🔧 BiFunction<F, S, Future<T>> 组合函数，输入为 F 和 S，输出为 Future<T>
     * @param <F>        💾 第一个异步结果 F
     * @param <S>        💾 第二个异步结果 S
     * @param <T>        🎯 组合函数的最终执行结果 T
     *
     * @return Future<T> 🌟 返回执行过的结果
     */
    public static <F, S, T> Future<T> combineT(final Supplier<Future<F>> supplierF, final Supplier<Future<S>> supplierS,
                                               final BiFunction<F, S, Future<T>> combinerOf) {
        return FnVertx.combineT(supplierF, supplierS, combinerOf);
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - List
     *
     * @param futures List<Future<T>> 📤 输入的异步结果列表
     * @param <T>     💾 泛型类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <T> Future<Boolean> combineB(final List<Future<T>> futures) {
        return FnVertx.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - Set
     *
     * @param futures Set<Future<T>> 📤 输入的异步结果集合
     * @param <T>     💾 泛型类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <T> Future<Boolean> combineB(final Set<Future<T>> futures) {
        return FnVertx.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - 同步集合 List
     *
     * @param source      📥 输入的同步集合
     * @param generateFun 🔧 生成异步操作的函数
     * @param <I>         💾 输入类型
     * @param <T>         💾 中间类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <I, T> Future<Boolean> combineB(final List<I> source, final Function<I, Future<T>> generateFun) {
        final List<Future<T>> futures = new ArrayList<>();
        source.stream().map(generateFun).forEach(futures::add);
        return FnVertx.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }

    /**
     * ✅ 组合函数 - 布尔结果版本 - 同步集合 Set
     *
     * @param source      📥 输入的同步集合
     * @param generateFun 🔧 生成异步操作的函数
     * @param <I>         💾 输入类型
     * @param <T>         💾 中间类型
     *
     * @return Future<Boolean> 🌟 返回执行状态，成功为 true
     */
    public static <I, T> Future<Boolean> combineB(final Set<I> source, final Function<I, Future<T>> generateFun) {
        final Set<Future<T>> futures = new HashSet<>();
        source.stream().map(generateFun).forEach(futures::add);
        return FnVertx.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
    }
}
