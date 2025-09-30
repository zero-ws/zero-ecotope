package io.zerows.extension.commerce.finance.uca.enter;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.eon.em.EmTran;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.unity.Ux;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author lang : 2024-01-18
 */
class MakerTrans implements Maker<String, FTrans> {
    @Override
    public Future<FTrans> buildFastAsync(final JsonObject data) {
        String indent = Ut.valueString(data, KName.INDENT);
        if (Ut.isNil(indent)) {
            indent = FmConstant.NUM.TRANS;
        }
        return this.buildAsync(data, indent);
    }

    @Override
    public Future<FTrans> buildAsync(final JsonObject data, final String indent) {
        /*
         * active, sigma, language
         * createdAt, createdBy
         * updatedAt, updatedBy
         * comment,
         *
         * amount
         */
        final FTrans trans = Ux.fromJson(data, FTrans.class);


        /*
         * 关闭预付
         * amountPre = 0.0
         * prepay = false
         */
        trans.setAmountPre(BigDecimal.ZERO);
        trans.setPrepay(Boolean.FALSE);
        trans.setStatus(EmTran.Status.FINISHED.name());

        return Ke.umIndent(trans, trans.getSigma(), indent, FTrans::setSerial).compose(generated -> {
            if (Objects.isNull(generated.getCode())) {
                generated.setCode(generated.getSerial());
            }
            return Ux.future(generated);
        });
    }
}
