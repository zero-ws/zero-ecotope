package io.zerows.core.web.validation.uca.rules;

import io.zerows.core.exception.WebException;
import io.zerows.core.web.model.atom.Rule;

import java.util.Collection;

class EmptyRuler extends BaseRuler {
    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        if (null != value && value instanceof Collection) {
            final Collection reference = (Collection) value;
            if (reference.isEmpty()) {
                error = this.failure(field, value, rule);
            }
        }
        return error;
    }
}
