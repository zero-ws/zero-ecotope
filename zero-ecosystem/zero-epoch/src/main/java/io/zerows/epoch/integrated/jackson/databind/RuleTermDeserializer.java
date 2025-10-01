package io.zerows.epoch.integrated.jackson.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.common.shared.normalize.KRuleTerm;
import io.zerows.epoch.support.UtBase;

import java.io.IOException;

public class RuleTermDeserializer extends JsonDeserializer<KRuleTerm> {
    @Override
    public KRuleTerm deserialize(final JsonParser parser,
                                 final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final String literal = node.toString();
        if (UtBase.isJArray(literal)) {
            final JsonArray array = new JsonArray(literal);
            return new KRuleTerm(array);
        } else {
            final String stringValue = node.asText();
            return new KRuleTerm(stringValue);
        }
    }
}
