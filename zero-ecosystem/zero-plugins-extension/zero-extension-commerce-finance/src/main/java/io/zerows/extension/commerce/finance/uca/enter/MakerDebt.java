package io.zerows.extension.commerce.finance.uca.enter;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlementItem;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.eon.em.EmDebt;
import io.zerows.extension.commerce.finance.uca.replica.IkWay;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import java.util.List;

/**
 * @author lang : 2024-01-22
 */
class MakerDebt implements Maker<List<FSettlementItem>, FDebt> {
    @Override
    public Future<FDebt> buildAsync(final JsonObject data, final List<FSettlementItem> items) {

        final FDebt debt = Ux.fromJson(data, FDebt.class);
        // UCA，此处要内部调用，因为这里会牵涉到类型的判断
        IkWay.ofSI2D().transfer(items, debt);

        final String type = debt.getType();
        final String indent = EmDebt.Type.DEBT.name().equals(type) ? FmConstant.NUM.DEBT : FmConstant.NUM.REFUND;

        return Ke.umIndent(debt, debt.getSigma(), indent, FDebt::setSerial).compose(generated -> {
            if (null == generated.getCode()) {
                generated.setCode(generated.getSerial());
            }
            return Ux.future(generated);
        });
    }
}
