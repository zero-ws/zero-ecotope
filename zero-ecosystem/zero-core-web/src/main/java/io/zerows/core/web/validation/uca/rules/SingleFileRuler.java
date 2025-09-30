package io.zerows.core.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.core.web.model.atom.Rule;

import java.util.Collection;

class SingleFileRuler extends BaseRuler {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        if (Collection.class.isAssignableFrom(value.getClass())) {
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
