package io.zerows.core.web.validation.uca.combine;

import io.zerows.core.exception.WebException;
import io.vertx.core.json.JsonObject;
import io.zerows.core.web.model.atom.Rule;
import io.zerows.core.web.validation.uca.rules.Ruler;

import java.util.List;
import java.util.Map;

public class JObjectRigor implements Rigor {

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
