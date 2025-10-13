package io.zerows.cosmic.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.platform.constant.VValue;

/**
 * <pre>
 *  {
 *      "type":"maxlength",
 *      "message":"xxx",
 *      "config":{
 *          "value":8
 *      }
 *  }
 * </pre>
 */
class RulerMaxLength extends RulerBase {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final WebRule rule) {
        WebException error = null;
        final int length = null == value ? VValue.ZERO : value.toString().length();
        final int max = rule.getConfig().getInteger("max");
        if (length > max) {
            error = this.failure(field, value, rule);
        }
        return error;
    }
}
