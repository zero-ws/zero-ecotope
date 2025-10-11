package io.zerows.epoch.bootplus.extension.uca.modello;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.LogOf;
import io.zerows.support.Ut;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.property.OComponent;
import io.zerows.specification.modeling.property.OExpression;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class OutCopy implements OComponent {
    private static final LogOf LOGGER = LogOf.get(OutCopy.class);

    @Override
    public Object after(final Kv<String, Object> kv, final HRecord record, final JsonObject combineData) {
        final JsonObject sourceNorm = Ut.valueJObject(combineData.getJsonObject(KName.SOURCE_NORM));
        if (Ut.isNotNil(sourceNorm)) {
            /*
             * Record Processing
             */
            final ConcurrentMap<String, OExpression> exprMap = IoHelper.afterExpression(combineData);
            Ut.<JsonArray>itJObject(sourceNorm, (array, field) -> {
                final Object value = record.get(field);
                if (Objects.nonNull(value)) {
                    final Set<String> copyFields = Ut.toSet(array);
                    copyFields.forEach(targetField -> {
                        if (record.isValue(targetField)) {
                            LOGGER.warn(Info.COPY_SKIP, targetField, record.identifier(), record.toJson());
                        } else {
                            /*
                             * Attach the get directly
                             */
                            if (exprMap.containsKey(targetField)) {
                                final OExpression expression = exprMap.get(targetField);
                                final Object normalized = expression.after(Kv.create(targetField, value));
                                record.attach(targetField, normalized);
                            } else {
                                record.attach(targetField, value);
                            }
                        }
                    });
                }
            });
        }
        return kv.value();
    }
}
