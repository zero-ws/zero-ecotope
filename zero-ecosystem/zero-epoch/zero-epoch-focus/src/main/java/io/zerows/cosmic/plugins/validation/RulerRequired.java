package io.zerows.cosmic.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.support.Ut;

/**
 * {
 * "type":"required",
 * "message":"xxx"
 * "config": "None"
 * }
 */
class RulerRequired extends RulerBase {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final WebRule rule) {
        WebException error = null;
        if (null == value || Ut.isNil(value.toString())) {
            // Single Field
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
