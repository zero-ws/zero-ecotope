package io.zerows.epoch.component.serialization;

import io.vertx.core.buffer.Buffer;

/**
 * Buffer
 */
class SaberBuffer extends SaberBase {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (Buffer.class == paramType) {
            final Buffer buffer = Buffer.buffer();
            buffer.appendString(literal);
            // Illegal base64 character 2f
            // buffer.appendBytes(literal.getBytes(Values.DEFAULT_CHARSET));
            return buffer;
        }
        return Buffer.buffer();
    }
}
