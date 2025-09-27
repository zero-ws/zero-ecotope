package io.zerows.core.web.validation.uca.rules;

import io.zerows.ams.constant.VValue;
import io.zerows.core.exception.WebException;
import io.zerows.core.web.model.atom.Rule;

/**
 * {
 * "type":"minlength",
 * "message":"xxx",
 * "config":{
 * "value":8
 * }
 * }
 */
class MinLengthRuler extends BaseRuler {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        final int length = null == value ? VValue.ZERO : value.toString().length();
        final int min = rule.getConfig().getInteger("min");
        if (length < min) {
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
