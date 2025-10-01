package io.zerows.epoch.integrated.jackson.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonArray;

import java.io.IOException;

/**
 * # 「Tp」Jackson Deserializer
 *
 * Came from `vert.x` internally to support `io.vertx.core.json.JsonArray` deserialization, ignored.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

    @Override
    public JsonArray deserialize(final JsonParser parser,
                                 final DeserializationContext context)
        throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return new JsonArray(node.toString());
    }
}
