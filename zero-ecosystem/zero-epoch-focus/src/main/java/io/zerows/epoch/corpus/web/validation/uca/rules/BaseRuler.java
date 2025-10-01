package io.zerows.epoch.corpus.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.atom.Rule;
import io.zerows.epoch.corpus.web.exception._60005Exception400ValidationRule;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

public abstract class BaseRuler implements Ruler {

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
