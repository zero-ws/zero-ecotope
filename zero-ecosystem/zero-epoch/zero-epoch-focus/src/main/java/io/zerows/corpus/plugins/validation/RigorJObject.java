package io.zerows.corpus.plugins.validation;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.model.Rule;

import java.util.List;
import java.util.Map;

public class RigorJObject implements Rigor {

    @Override
    public WebException verify(final Map<String, List<Rule>> rulers,
                               final Object body) {
        WebException error = null;
        if (!rulers.isEmpty()) {
            // Extract first element to JsonObject
            if (null != body) {
                final JsonObject data = (JsonObject) body;
                // Verify the whole JsonObject
                for (final String field : rulers.keySet()) {
                    final Object value = data.getValue(field);
                    final List<Rule> rules = rulers.get(field);
                    // Verify each field.
                    error = Ruler.verify(rules, field, value);
                    if (null != error) {
                        break;
                    }
                }
            }
        }
        return error;
    }
}
