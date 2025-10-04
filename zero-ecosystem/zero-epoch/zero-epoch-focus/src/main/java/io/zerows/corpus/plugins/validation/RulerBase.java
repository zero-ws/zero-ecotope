package io.zerows.corpus.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.component.log.OLog;
import io.zerows.epoch.corpus.model.Rule;
import io.zerows.corpus.plugins.validation.exception._60005Exception400ValidationRule;
import io.zerows.support.Ut;

public abstract class RulerBase implements Ruler {

    protected WebException failure(
        final String field,
        final Object value,
        final Rule rule) {
        final String message = rule.getMessage();
        return new _60005Exception400ValidationRule(field, value, message);
    }

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
