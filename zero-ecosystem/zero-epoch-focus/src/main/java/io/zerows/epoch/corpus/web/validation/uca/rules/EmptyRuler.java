package io.zerows.epoch.corpus.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.corpus.model.atom.Rule;

import java.util.Collection;

class EmptyRuler extends BaseRuler {
    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        if (value instanceof final Collection<?> reference) {
            if (reference.isEmpty()) {
                error = this.failure(field, value, rule);
            }
        }
        return error;
    }
}
