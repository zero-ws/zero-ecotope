package io.zerows.integrated.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * # 「Tp」Jackson Serializer
 * <p>
 * This serializer came from `vert.x` internally. In vert.x framework the datetime object will be converted to
 * `java.time.Instant` as uniform, in this situation it could provide developers to simplify `Date Format` processing
 * in json specification.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class InstantSerializer extends JsonSerializer<Instant> {
    @Override
    public void serialize(final Instant value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeString(ISO_INSTANT.format(value));
    }
}
