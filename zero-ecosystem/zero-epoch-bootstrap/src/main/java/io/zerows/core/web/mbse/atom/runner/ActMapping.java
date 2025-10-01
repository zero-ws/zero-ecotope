package io.zerows.core.web.mbse.atom.runner;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.EmAop;
import io.zerows.common.datamation.KMap;
import io.zerows.core.util.Ut;
import io.zerows.feature.web.utility.uca.FieldMapper;
import io.zerows.feature.web.utility.uca.Mapper;
import io.zerows.specification.modeling.HRecord;

import java.io.Serializable;
import java.util.Objects;

public abstract class ActMapping implements Serializable {

    private final transient Mapper mapper = new FieldMapper();

    /*
     * ActIn
     */
    protected HRecord getRecord(final Object input, final HRecord definition, final KMap mapping) {
        final HRecord record = definition.createNew();
        if (input instanceof String) {
            final String key = (String) input;
            record.key(key);
        } else if (input instanceof JsonObject) {
            final JsonObject dataRef = (JsonObject) input;
            if (Ut.isNotNil(dataRef)) {
                /*
                 * Set current data to `Record` with `DualMapping`
                 * Check whether mount dual mapping first here
                 *
                 * Passive Only
                 */
                if (this.isBefore(mapping)) {
                    final JsonObject normalized = this.mapper.in(dataRef, mapping.child());
                    record.set(normalized);
                } else {
                    record.set(dataRef.copy());
                }
            }
        }
        return record;
    }

    /*
     * Whether it's before automatic
     */
    protected boolean isBefore(final KMap mapping) {
        if (Objects.isNull(mapping)) {
            return false;
        }
        if (EmAop.Effect.BEFORE != mapping.getMode() && EmAop.Effect.AROUND != mapping.getMode()) {
            return false;
        }
        return mapping.valid();
    }

    /*
     * Whether it's after automatic
     */
    protected boolean isAfter(final KMap mapping) {
        if (Objects.isNull(mapping)) {
            return false;
        }
        if (EmAop.Effect.AFTER != mapping.getMode() && EmAop.Effect.AROUND != mapping.getMode()) {
            return false;
        }
        return mapping.valid();
    }

    protected Mapper mapper() {
        return this.mapper;
    }
}
