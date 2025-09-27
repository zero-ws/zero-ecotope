package io.zerows.plugins.integration.feign.codec;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Type;

public class JsonObjectEncoder implements Encoder {
    @Override
    public void encode(final Object o, final Type type,
                       final RequestTemplate requestTemplate) throws EncodeException {
        if (JsonObject.class == type && null != o) {
            final JsonObject item = (JsonObject) o;
            requestTemplate.body(item.encode());
        }
    }
}
