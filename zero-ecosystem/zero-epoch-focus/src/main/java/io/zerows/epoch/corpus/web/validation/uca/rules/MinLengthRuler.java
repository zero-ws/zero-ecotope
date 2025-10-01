package io.zerows.epoch.corpus.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.corpus.model.atom.Rule;

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
