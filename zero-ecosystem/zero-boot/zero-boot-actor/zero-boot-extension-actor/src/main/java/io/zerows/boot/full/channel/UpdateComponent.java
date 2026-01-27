package io.zerows.boot.full.channel;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.full.base.AbstractHOne;
import io.zerows.epoch.metadata.Apt;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.mbse.metadata.ActIn;


/**
 * ## 「Channel」更新记录通道
 * <p>
 * ### 1. 基本介绍
 * <p>
 * 不带任何<strong>集成功能</strong>的标准更新组件，支持如下功能：
 * <p>
 * - 传入数据，更新模型对应数据
 * - 请求`PUT /api/ox/:identifier/:key`
 * <p>
 * ### 2. 通道详细
 * <p>
 * - 类型：ADAPTOR
 * <p>
 * ### 3. 请求格式
 * <p>
 * ```json
 * // <pre><code class="json">
 * {
 *     "key": "主键",
 *     "name": "名称",
 *     "field2": "...",
 *     "field3": "..."
 * }
 * // </code></pre>
 * ```
 * <p>
 * ### 4. 响应格式
 * <p>
 * ```json
 * // <pre><code class="json">
 * {
 *     "data": {
 *          "key": "主键",
 *          "name": "名称",
 *          "field2": "...",
 *          "field3": "..."
 *     }
 * }
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class UpdateComponent extends AbstractHOne {

    @Override
    public Future<Apt> transferIn(final ActIn request) {
        return this.inUpdate(request);
    }

    /*
     * transferIn 中的数据为旧数据
     */
    @Override
    public Future<JsonObject> transferAsync(final Apt apt, final ActIn request,
                                            final DataAtom atom) {
        /* 压缩数据 */
        return apt.<JsonObject>dataIAsync()

            /* 写二期库 */
            .compose(pushed -> this.completer(atom).update(pushed))

            /* 比较生成变更历史 */
            .compose(newRecord -> this.trackAsyncU(apt.dataO(), newRecord, atom));
    }
}
