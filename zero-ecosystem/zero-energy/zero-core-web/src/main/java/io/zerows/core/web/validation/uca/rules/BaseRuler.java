package io.zerows.core.web.validation.uca.rules;

import io.zerows.core.exception.WebException;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.Rule;
import io.zerows.core.web.validation.exception._400ValidationRuleException;
import io.zerows.module.metadata.uca.logging.OLog;

public abstract class BaseRuler implements Ruler {

    protected WebException failure(
        final String field,
        final Object value,
        final Rule rule) {
        final String message = rule.getMessage();
        final WebException error = new _400ValidationRuleException(
            this.getClass(), field, value, message);
        error.readable(message);
        this.logger().info(INFO.MSG_FAILURE, error.toJson());
        return error;
    }

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
