package io.zerows.extension.runtime.crud.uca.input.file;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.mbse.metadata.KModule;
import io.zerows.epoch.metadata.KField;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.program.Ux;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 附件的添加、删除、查询、修改等核心方法，不同子类实现不同的附件方法，抽象类中包含了和
 * 附件相关的通用编排方法，实现附件本身的增删改
 *
 * @author lang : 2023-08-04
 */
abstract class FileAction implements Pre {
    /**
     * 将原始的 Ix 中的 fileFn 直接抽象出来，实现独立的附件管理相关方法
     *
     * @param in     IxMod
     * @param fileFn BiFunction<JsonObject, JsonArray, Future<JsonArray>>
     *
     * @return {@link Function}
     */
    protected Function<JsonObject, Future<JsonObject>> actionFn(
        final IxMod in,
        final BiFunction<JsonObject, JsonArray, Future<JsonArray>> fileFn) {
        return data -> {
            final KModule module = in.module();
            final KField field = module.getField();
            if (Objects.isNull(field)) {
                /*
                 * KField of new attachment:
                 * {
                 *      "attachment": [
                 *          {
                 *              "field": "model field name",
                 *              "condition": {
                 *                  "field1": "value1",
                 *                  "field2": "value2"
                 *              }
                 *          }
                 *      ]
                 * }
                 */
                return Ux.future(data);
            }
            /*
             * 此处得到的数据结构有必要说明，`condition` 的定义中包含了 JEXL 的表达式，此处构造的结构
             * 如：
             * {
             *     "field1": "condition1",
             *     "field2": "condition2",
             *     "field3": "condition3"
             * }
             * 且此处只是提取定义，并且根据输入的 data 对 JEXL 执行表达式解析，解析的最终结果会填充到
             * 参数中形成最终的查询参数，并提取附件。附件的提取在此处很重要，由于附件操作过程中会包含
             * - 添加
             * - 删除
             * - 更新（替换）
             * 这三种操作都依赖原始存在的附件记录，只有将原始的附件记录提取出来才能执行后续的操作。
             */
            final ConcurrentMap<String, JsonObject> attachmentMap = field.fieldFile();
            return Ke.mapFn(attachmentMap, fileFn).apply(data);
        };
    }
}
