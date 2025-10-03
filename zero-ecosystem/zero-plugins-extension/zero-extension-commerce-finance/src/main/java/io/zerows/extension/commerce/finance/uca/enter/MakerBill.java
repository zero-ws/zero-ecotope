package io.zerows.extension.commerce.finance.uca.enter;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.Objects;

/**
 * <pre><code>
 *     单个账单的初始化流程
 *     From: {@link JsonObject}
 *     To: {@link FBill}
 * </code></pre>
 *
 * @author lang : 2024-01-18
 */
class MakerBill implements Maker<String, FBill> {

    /**
     * 「账单」
     * 初始化账单，序号配置直接从 indent 中提取，此处的 data 数据结构如：
     * <pre><code>
     *     {
     *         "indent": "X_NUMBER 中的 code 定义"
     *     }
     * </code></pre>
     * 最终生成的序号存储在 code / serial 中，内置调用
     *
     * @param data   输入数据
     * @param indent 序号
     *
     * @return {@link FBill}
     */
    @Override
    public Future<FBill> buildAsync(final JsonObject data, final String indent) {
        Objects.requireNonNull(indent);
        // Bill 构造
        final FBill bill = Ux.fromJson(data, FBill.class);
        // 序号构造
        return Ke.umIndent(bill, bill.getSigma(), indent, FBill::setSerial)
            .compose(generated -> {
                if (Objects.isNull(generated.getCode())) {
                    generated.setCode(generated.getSerial());
                }
                return Ux.future(generated);
            });
    }

    @Override
    public Future<FBill> buildFastAsync(final JsonObject data) {
        final String indent = Ut.valueString(data, KName.INDENT);
        return this.buildAsync(data, indent);
    }
}
