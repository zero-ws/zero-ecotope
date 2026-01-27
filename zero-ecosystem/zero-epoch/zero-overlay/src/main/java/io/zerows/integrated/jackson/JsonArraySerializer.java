package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.vertx.core.json.JsonArray;

import java.io.IOException;

/**
 * # 「Tp」Jackson Serializer
 * <p>
 * Came from `vert.x` internally to support `io.vertx.core.json.JsonArray` serialization, ignored.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class JsonArraySerializer extends JsonSerializer<JsonArray> {
    @Override
    public void serialize(final JsonArray value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeObject(value.getList());
    }
}
