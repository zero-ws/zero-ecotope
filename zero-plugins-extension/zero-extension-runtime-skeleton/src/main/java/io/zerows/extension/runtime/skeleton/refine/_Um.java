package io.zerows.extension.runtime.skeleton.refine;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author lang : 2023-06-07
 */
class _Um extends _Key {
    protected _Um() {
    }

    /**
     * 将数据从 input 拷贝到 output 中，内部调用在这种模式下有几个限制
     * <pre><code>
     *     1. 此接口不支持映射关系 `pojo/xxx.yml` 映射模型
     *     2. 由于 isUpdated = false，作用如下四个字段
     *        createdAt
     *        createdBy
     *        updatedAt
     *        updatedBy
     * </code></pre>
     *
     * @param output 目标对象
     * @param input  源对象
     * @param <T>    源泛型类型
     * @param <I>    目标泛型类型
     */
    public static <T, I> void umCreated(final I output, final T input) {
        KeEnv.audit(output, null, input, null, false);
    }

    /**
     * 将数据从 input 拷贝到 output 中，内部调用在这种模式下有几个限制
     * <pre><code>
     *     1. 映射关系
     *        - 不支持目标映射关系 `pojo/xxx.yml` 映射模型
     *        - 支持源映射关系 `pojo/xxx.yml` 映射模型
     *     2. 由于 isUpdated = false，作用如下四个字段
     *        createdAt
     *        createdBy
     *        updatedAt
     *        updatedBy
     * </code></pre>
     *
     * @param output 目标对象
     * @param input  源对象
     * @param pojo   源映射模型
     * @param <T>    源泛型类型
     * @param <I>    目标泛型类型
     */
    public static <T, I> void umCreated(final I output, final T input, final String pojo) {
        KeEnv.audit(output, null, input, pojo, false);
    }

    /**
     * 将数据从 input 拷贝到 output 中，内部调用在这种模式下有几个限制
     * <pre><code>
     *     1. 映射关系
     *        - 支持目标映射关系 `pojo/xxx.yml` 映射模型
     *        - 不支持源映射关系 `pojo/xxx.yml` 映射模型
     *     2. 由于 isUpdated = false，作用如下四个字段
     *        createdAt
     *        createdBy
     *        updatedAt
     *        updatedBy
     * </code></pre>
     *
     * @param output 目标对象
     * @param pojo   目标映射模型
     * @param input  源对象
     * @param <T>    源泛型类型
     * @param <I>    目标泛型类型
     */
    public static <T, I> void umCreated(final I output, final String pojo, final T input) {
        KeEnv.audit(output, pojo, input, null, false);
    }

    /**
     * 将数据从 input 拷贝到 output 中，内部调用在这种模式下有几个限制
     * <pre><code>
     *     1. 映射关系
     *        - 支持目标映射关系 `pojo/xxx.yml` 映射模型
     *        - 支持源映射关系 `pojo/xxx.yml` 映射模型
     *     2. 由于 isUpdated = false，作用如下四个字段
     *        createdAt
     *        createdBy
     *        updatedAt
     *        updatedBy
     * </code></pre>
     *
     * @param output  目标对象
     * @param outPojo 目标映射模型
     * @param input   源对象
     * @param inPojo  源映射模型
     * @param <T>     源泛型类型
     * @param <I>     目标泛型类型
     */
    public static <T, I> void umCreated(final I output, final String outPojo, final T input, final String inPojo) {
        KeEnv.audit(output, outPojo, input, inPojo, false);
    }

    public static void umCreatedJ(final JsonObject body, final User user) {
        KeEnv.auditJ(body, user);
    }

    public static <T, I> void umUpdated(final I output, final T input) {
        KeEnv.audit(output, null, input, null, true);
    }

    public static <T, I> void umUpdated(final I output, final T input, final String pojo) {
        KeEnv.audit(output, null, input, pojo, true);
    }

    public static <T, I> void umUpdated(final I output, final String pojo, final T input) {
        KeEnv.audit(output, pojo, input, null, true);
    }

    public static <T, I> void umUpdated(final I output, final String outPojo, final T input, final String inPojo) {
        KeEnv.audit(output, outPojo, input, inPojo, true);
    }

    public static Future<JsonObject> umIndent(final JsonObject data, final String code) {
        return KeEnv.indent(data, code);
    }

    public static Future<JsonArray> umIndent(final JsonArray data, final String code) {
        return KeEnv.indent(data, code);
    }

    public static <T> Future<T> umIndent(final T input, final Function<T, String> fnSigma,
                                         final String code,
                                         final BiConsumer<T, String> fnConsumer) {
        final String sigma = fnSigma.apply(input);
        return KeEnv.indent(input, sigma, code, fnConsumer);
    }

    public static <T> Future<T> umIndent(final T input, final String sigma,
                                         final String code,
                                         final BiConsumer<T, String> fnConsumer) {
        return KeEnv.indent(input, sigma, code, fnConsumer);
    }

    public static <T> Future<List<T>> umIndent(final List<T> input, final Function<List<T>, String> fnSigma,
                                               final String code,
                                               final BiConsumer<T, String> fnConsumer) {
        final String sigma = fnSigma.apply(input);
        return KeEnv.indent(input, sigma, code, fnConsumer);
    }

    /**
     * 直接根据输入的列表分别设置对应的序列号，此处序号设置是统一的
     * <pre><code>
     *     1. 根据 code 从 X_NUMBER 中生成序号，生成序号数量为 input 的长度 {@link List#size()}
     *     2. 生成序号的范围为 sigma 划定的范围
     *        简单说就是 sigma -> X_NUMBER -> [serial] x N
     *     3. 将生成的序号对齐：前进先出的方式执行设置函数
     *     4. 设置函数使用 fnConsumer 来完成，此处的 fnConsumer 为 BiConsumer
     *        - 第一个参数是 List 中的元素
     *        - 第二个参数就是对齐过后的序号
     * </code></pre>
     *
     * @param input      输入列表
     * @param sigma      sigma值，统一标识符
     * @param code       序列号定义
     * @param fnConsumer 序列号设置函数，此处设置函数针对每一个元素
     * @param <T>        泛型类型
     *
     * @return {@link Future} 已经设置好的列表
     */
    public static <T> Future<List<T>> umIndent(final List<T> input, final String sigma,
                                               final String code,
                                               final BiConsumer<T, String> fnConsumer) {
        return KeEnv.indent(input, sigma, code, fnConsumer);
    }

    public static Future<JsonObject> umJData(final JsonObject config, final JsonObject params) {
        return KeEnv.daoJ(config, params);
    }

    public static Future<JsonObject> umAData(final JsonObject config, final JsonObject params) {
        return KeEnv.daoJ(config, params);
    }

    public static Future<JsonArray> umALink(final String field, final String key, final Class<?> daoCls) {
        return KeEnv.daoR(field, key, daoCls);
    }

    public static <T> Future<List<T>> umALink(final String field, final String key, final Class<?> daoCls,
                                              final Function<T, Integer> priorityFn) {
        return KeEnv.daoR(field, key, daoCls, priorityFn);
    }

    public static Future<JsonObject> umUser(final JsonObject input, final JsonObject config) {
        return KeUser.umUser(input, config);
    }

    public static Future<JsonObject> umUser(final JsonObject input) {
        final JsonObject config = Ut.valueJObject(input, KName.__.USER);
        return KeUser.umUser(input, config);
    }
}
