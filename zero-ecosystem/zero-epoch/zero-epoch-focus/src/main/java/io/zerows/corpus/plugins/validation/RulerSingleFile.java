package io.zerows.corpus.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.corpus.model.Rule;

import java.util.Collection;

class RulerSingleFile extends RulerBase {

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
