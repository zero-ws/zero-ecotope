package io.zerows.core.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.Rule;
import io.zerows.epoch.web.exception._60005Exception400ValidationRule;
import io.zerows.module.metadata.uca.logging.OLog;

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
