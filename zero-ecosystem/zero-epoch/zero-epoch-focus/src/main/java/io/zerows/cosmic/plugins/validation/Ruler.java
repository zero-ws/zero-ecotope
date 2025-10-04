package io.zerows.cosmic.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.zerows.cortex.metadata.WebRule;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Ruler {
    ConcurrentMap<String, Ruler> CC_SKELETON = new ConcurrentHashMap<String, Ruler>() {
        {
            this.put("required", new RulerRequired());
            this.put("length", new RulerLength());
            this.put("minlength", new RulerMinLength());
            this.put("maxlength", new RulerMaxLength());
            this.put("empty", new RulerEmpty());
            this.put("singlefile", new RulerSingleFile());
        }
    };

    static Ruler get(final String type) {
        return CC_SKELETON.get(type);
    }

    static WebException verify(final Collection<WebRule> rules,
                               final String field,
                               final Object value) {
        WebException error = null;
        for (final WebRule rule : rules) {
            final Ruler ruler = get(rule.getType());
            if (null != ruler) {
                error = ruler.verify(field, value, rule);
            }
            // Error found
            if (null != error) {
                break;
            }
        }
        return error;
    }

    /**
     * Verify each field for @BodyParam
     *
     * @param field Input field of the data structure
     * @param value The input field reflect value literal
     * @param rule  The rule that has been defined.
     *
     * @return WebException that the validated error here.
     */
    WebException verify(final String field,
                        final Object value,
                        final WebRule rule);
}
