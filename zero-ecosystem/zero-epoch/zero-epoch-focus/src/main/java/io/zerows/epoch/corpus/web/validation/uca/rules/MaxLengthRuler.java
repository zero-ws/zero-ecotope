package io.zerows.epoch.corpus.web.validation.uca.rules;

import io.r2mo.typed.exception.WebException;
import io.zerows.constant.VValue;
import io.zerows.epoch.corpus.model.Rule;

/**
 * {
 * "type":"minlength",
 * "message":"xxx",
 * "config":{
 * "value":8
 * }
 * }
 */
class MaxLengthRuler extends BaseRuler {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        final int length = null == value ? VValue.ZERO : value.toString().length();
        final int max = rule.getConfig().getInteger("max");
        if (length > max) {
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
