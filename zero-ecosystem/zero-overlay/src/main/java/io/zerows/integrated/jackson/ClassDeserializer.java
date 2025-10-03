package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.zerows.support.UtBase;

import java.io.IOException;

/**
 * # 「Tp」Jackson Deserializer
 *
 * Zero designed the cache pool for class that will be stored into `ConcurrentMap` ( Default implementation ),
 * It means that this class could be loaded once and usage multi-times. Here I provide default deserializer to convert
 * `java.lang.String` to `java.lang.Class<?>` to simplify the clazz look-up in stored.
 *
 * Actually, there exist smart method such as {@linkplain UtBase#instance Ut.instance} and
 * {@linkplain UtBase#clazz Ut.clazz}, with those APIs in `Utility X`, the developer could do java
 * reflection very fast without other consideration.
 *
 * This deserializer is reverted component to {@link ClassSerializer}.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ClassDeserializer extends JsonDeserializer<Class<?>> { // NOPMD

    @Override
    public Class<?> deserialize(final JsonParser parser,
                                final DeserializationContext context)
        throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return UtBase.clazz(node.asText().trim(), null);
    }
}
