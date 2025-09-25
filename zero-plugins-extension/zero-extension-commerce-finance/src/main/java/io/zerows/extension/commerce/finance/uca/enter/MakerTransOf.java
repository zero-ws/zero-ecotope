package io.zerows.extension.commerce.finance.uca.enter;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTransOf;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-01-25
 */
class MakerTransOf implements Maker<FTrans, List<FTransOf>> {
    @Override
    public Future<List<FTransOf>> buildAsync(final JsonObject data, final FTrans trans) {
        /*
         * {
         *     "type": "xxx",
         *     "comment": "xxx",
         *     "keys": []
         * }
         */
        Objects.requireNonNull(trans);
        final Set<String> keys = Ut.toSet(Ut.valueJArray(data, KName.KEYS));
        final List<FTransOf> transOfList = new ArrayList<>();

        final String type = Ut.valueString(data, KName.TYPE);
        final String comment = Ut.valueString(data, KName.COMMENT);

        keys.forEach(key -> {
            final FTransOf transOf = new FTransOf();
            transOf.setTransId(trans.getKey());
            transOf.setObjectId(key);

            transOf.setObjectType(type);
            transOf.setComment(comment);
            transOfList.add(transOf);
        });
        return Ux.future(transOfList);
    }
}
