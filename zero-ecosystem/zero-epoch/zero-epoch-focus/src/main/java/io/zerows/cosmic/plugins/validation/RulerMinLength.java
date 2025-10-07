package io.zerows.cosmic.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.platform.constant.VValue;

/**
 * {
 * "type":"minlength",
 * "message":"xxx",
 * "config":{
 * "get":8
 * }
 * }
 */
class RulerMinLength extends RulerBase {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final WebRule rule) {
        WebException error = null;
        final int length = null == value ? VValue.ZERO : value.toString().length();
        final int min = rule.getConfig().getInteger("min");
        if (length < min) {
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
