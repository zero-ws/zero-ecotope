package io.zerows.extension.runtime.crud.util;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.aop.Aspect;
import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;
import io.zerows.mbse.metadata.KModule;
import io.zerows.epoch.metadata.KField;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.platform.metadata.Kv;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.metadata.HMetaAtom;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Ix {
    // --------------------------------- New Version

    /**
     * 列解析器，直接根据提供的值解析列，主要针对 metadata 格式的列处理，如
     * <pre><code>
     *     1. 字符串格式：`name,名称`
     *        最终会解析成 `name = 名称`
     *     2. 若是 {@link JsonObject}，根据 `key` 和 `get` 解析
     *        带 metadata 的解析，格式如字符串格式
     *        否则直接使用原生态格式
     * </code></pre>
     *
     * @param value 被解析的列值
     *
     * @return {@link Kv} 列解析结果
     */
    public static Kv<String, String> onColumn(final Object value) {
        return IxData.column(value);
    }


    /**
     * 保存视图时受影响的URI计算，目前受到影响的URI主要如下：
     * <pre><code>
     *     1. 针对视图执行缓存更新时，会影响查询视图的相关信息
     *     2. 现阶段的查询视图影响包含：
     *        /api/{0}/search
     * </code></pre>
     *
     * @param in {@link IxMod} 模型对象
     *
     * @return {@link Kv} 影响的URI信息
     */
    public static Kv<String, HttpMethod> onImpact(final IxMod in) {
        return IxData.impact(in);
    }

    /**
     * 标识规则的解析器，二维矩阵专用解析器，针对配置
     * <pre><code>
     *     {
     *         "unique": [
     *             []
     *         ]
     *     }
     * </code></pre>
     * 分组标识规则，分成若干组，第一位表示每一组的属性集
     *
     * @param field {@link KField} 字段定义信息
     *
     * @return {@link JsonArray} 解析结果
     */
    public static JsonArray onMatrix(final KField field) {
        return IxData.matrix(field);
    }

    /**
     * 参数解析器，特殊参数解析流程，解析当前模块所需的参数，主要用于填充 module 参数
     * <pre><code>
     *     当前版本中，参数解析按如下流程处理：
     *     1. 检查参数中是否有 module 参数，没有 module 参数时触发
     *     2. 若 connect 存在，则 module 填充的是 connect 被连接模块的 identifier
     *        否则 connect 不存在，则 module 填充的是当前模块的 identifier
     * </code></pre>
     *
     * @param in {@link IxMod} 模型对象
     *
     * @return {@link JsonObject} 解析结果
     */
    public static JsonObject onParameters(final IxMod in) {
        return IxData.parameters(in);
    }

    /**
     * 根据当前激活的模块，计算所需的 {@link HMetaAtom} 的元元模型对象
     *
     * @param active  当前激活的模块
     * @param columns 当前激活的模块的列信息
     *
     * @return {@link HMetaAtom} 计算结果
     */
    public static HMetaAtom onAtom(final IxMod active, final JsonArray columns) {
        return IxData.atom(active, columns);
    }

    // --------------------------------- Function Part

    /**
     * 顺序执行，函数流，上一个函数的输出是下一个函数的输入
     * <pre><code>
     *     Tool    -> executors[0] -> T0
     *     T0   -> executors[1] -> T1
     *     T1   -> ...          -> Tn
     * </code></pre>
     *
     * @param input     输入参数
     * @param in        {@link IxMod} 模型对象
     * @param executors 执行链
     * @param <T>       输入类型
     *
     * @return {@link Future} 执行结果
     */
    @SafeVarargs
    public static <T> Future<T> pass(final T input, final IxMod in, final BiFunction<T, IxMod, Future<T>>... executors) {
        return IxFn.pass(input, in, executors);
    }

    @SafeVarargs
    public static <T> Future<T> park(final T input, final IxMod in, final BiFunction<T, IxMod, Future<T>>... executors) {
        return IxFn.park(input, in, executors);
    }

    @SafeVarargs
    public static Future<JsonObject> peekJ(final JsonObject inputJ, final IxMod in, final BiFunction<JsonObject, IxMod, Future<JsonObject>>... executors) {
        return IxFn.peek(inputJ, in, JsonObject::new, executors);
    }

    public static <T> Function<T, Future<T>> aop(
        final KModule module, final BiFunction<Aspect, Function<T, Future<T>>, Function<T, Future<T>>> aopFn,
        final Function<T, Future<T>> executor) {
        return IxFn.aop(module, aopFn, executor);
    }

    // --------------------------------- Serialization / Deserialization System
    public static <T> Future<T> deserializeT(final JsonObject data, final KModule config) {
        final T reference = IxSerialize.deserializeT(data, config);
        return Ux.future(reference);
    }

    public static <T> Future<List<T>> deserializeT(final JsonArray data, final KModule config) {
        return Ux.future(IxSerialize.deserializeT(data, config));
    }

    public static <T> JsonObject serializeJ(final T input, final KModule config) {
        return IxSerialize.serializeJ(input, config);
    }

    public static <T> JsonArray serializeA(final List<T> input, final KModule config) {
        return IxSerialize.serializeA(input, config);
    }

    public static JsonObject serializeP(final JsonObject pageData, final KModule active, final KModule standBy) {
        return IxSerialize.serializeP(pageData, active, standBy);
    }

    // --------------------------------- Logger Part
    public interface LOG {

        String MODULE = "Εκδήλωση";

        LogModule Filter = Log.modulat(MODULE).extension("Filter");
        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Rest = Log.modulat(MODULE).extension("Rest");
        LogModule Web = Log.modulat(MODULE).extension("Web");
        LogModule Dao = Log.modulat(MODULE).extension("Dao");
        LogModule Verify = Log.modulat(MODULE).extension("Verify");
    }
}
