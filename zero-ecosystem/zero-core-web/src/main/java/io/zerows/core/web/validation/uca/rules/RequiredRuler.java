package io.zerows.core.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.Rule;

/**
 * {
 * "type":"required",
 * "message":"xxx"
 * "config": "None"
 * }
 */
class RequiredRuler extends BaseRuler {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        if (null == value || Ut.isNil(value.toString())) {
            // Single Field
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
