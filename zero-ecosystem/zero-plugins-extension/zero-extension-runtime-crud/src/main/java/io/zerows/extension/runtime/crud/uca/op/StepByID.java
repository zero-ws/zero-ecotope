package io.zerows.extension.runtime.crud.uca.op;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.ADB;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxJunc;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.next.Co;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.mbse.metadata.KModule;
import io.zerows.program.Ux;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class StepByID implements Agonic {
    @Override
    @SuppressWarnings("all")
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        // 提取主模型
        return fetchAsync(input, in).compose(dataJ -> {
            if (Objects.isNull(dataJ)) {
                /*
                 * 截断返回，直接返回 204 的正常响应，实际就是 {}
                 * - 如果主记录无法提取数据返回 204
                 * - 如果关联记录无法提取数据则直接考虑 {} 返回执行编排
                 */
                return IxReply.success204Pre();
            }


            // 动态连接，根据输入计算连接模型
            final IxJunc junc = IxJunc.of(in);
            final IxMod connect = junc.connect(dataJ);
            if (Objects.isNull(connect)) {
                // 截断返回，此处只返回主模型
                return Ux.future(dataJ);
            }

            // 针对连接模块的处理流程
            return this.nextAsync(
                input,             // 基础输入数据
                in,                // 此处根据 in 计算 connect，所以基础参数应该是 in，并非 connect
                // 连接模块依旧调用 fetchAsync 方法，只是切换模块对象
                params -> this.fetchAsync(params, connect),
                dataJ              // 主模型数据
            );
        });
    }

    /**
     * 中间方法，根据三个位数的数据生成最终的输出结果
     * <pre><code>
     *     1. inputJ：最早输入的数据信息
     *     2. in：当前模块信息
     *     3. dataJ：主模型数据
     * </code></pre>
     *
     * @param inputJ {@link JsonObject} 输入数据
     * @param in     {@link IxMod} 当前模块
     * @param dataJ  {@link JsonObject} 主模型数据
     *
     * @return {@link Future}<{@link JsonObject}>
     */
    @SuppressWarnings("all")
    private Future<JsonObject> nextAsync(final JsonObject inputJ,
                                         final IxMod in,
                                         final Function<JsonObject, Future<JsonObject>> executorFn,
                                         final JsonObject dataJ) {
        // 构造核心组件
        final Co<JsonObject, JsonObject, JsonObject, JsonObject> co =
            Co.nextQ(in, false);

        return co.next(inputJ, dataJ)                       // 从 active -> standBy 的转换
            .compose(executorFn)                            // 执行函数
            .compose(standBy -> co.ok(dataJ, standBy));     // 响应数据合并 active + standBy 生成最终响应
    }

    /**
     * 单记录读取，主要分为两部分
     * <pre><code>
     *     1. 根据输入条件直接读取记录数据
     *        - 记录不存在时返回 null
     *        - 如果记录存在则返回 {}
     *     2. 若记录存在继续在 {} 中提取附件数据，提取的附件会追加到当前记录中
     * </code></pre>
     *
     * @param criteria 输入条件
     * @param in       {@link IxMod} 可代表主模块，也可以代表连接模块
     *
     * @return {@link Future}<{@link JsonObject}>
     */
    private Future<JsonObject> fetchAsync(final JsonObject criteria, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchOneAsync(criteria).compose(queried -> {
            if (Objects.isNull(queried)) {
                // null 返回，上层会转换成 204
                return Ux.future();
            }
            // 针对格式提取附件数据，提取附件数据之前先将 queried 转换成 InJson
            final KModule module = in.module();
            final JsonObject active = Ix.serializeJ(queried, module);

            // 附件提取：追加到数据节点
            return Pre.fileData().inJAsync(active, in);
        });
    }
}
