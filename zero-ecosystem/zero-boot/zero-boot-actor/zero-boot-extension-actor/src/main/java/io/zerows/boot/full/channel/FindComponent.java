package io.zerows.boot.full.channel;

import io.vertx.core.Future;
import io.zerows.boot.full.base.AbstractHOne;
import io.zerows.mbse.metadata.ActIn;
import io.zerows.mbse.metadata.ActOut;

/**
 * ## 「Channel」读取数据通道
 * <p>
 * ### 1. 基本介绍
 * <p>
 * - 根据 key 值读取记录集
 * - 请求`GET /api/ox/:identifier/:key`
 * <p>
 * ### 2. 通道详细
 * <p>
 * - 类型：ADAPTOR
 * <p>
 * ### 3. 请求格式
 * <p>
 * 无请求体（Body）数据，直接传入identifier读取。
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
public class FindComponent extends AbstractHOne {
    @Override
    public Future<ActOut> transferAsync(final ActIn request) {
        final String key = this.activeKey(request);
        return this.fetchFull(key).compose(ActOut::future);
    }
}
