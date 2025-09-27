package io.zerows.core.web.validation.uca.rules;

import io.zerows.core.exception.WebException;
import io.vertx.core.json.JsonObject;
import io.zerows.core.web.model.atom.Rule;

class LengthRuler extends BaseRuler {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final Rule rule) {
        WebException error = null;
        // Extract length
        final JsonObject config = rule.getConfig();
        if (config.containsKey("max")) {
            final Ruler ruler = Ruler.get("maxlength");
            error = ruler.verify(field, value, rule);
        }
        if (null == error && config.containsKey("min")) {
            final Ruler ruler = Ruler.get("minlength");
            error = ruler.verify(field, value, rule);
        }
        return error;
    }
}
