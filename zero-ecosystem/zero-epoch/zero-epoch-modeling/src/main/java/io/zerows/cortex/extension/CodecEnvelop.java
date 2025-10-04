package io.zerows.cortex.extension;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

/**
 * Codec to transfer envelop
 */
public final class CodecEnvelop implements MessageCodec<Envelop, Envelop> {

    @Override
    public void encodeToWire(final Buffer buffer,
                             final Envelop message) {
        buffer.appendBytes(Ut.toBytes(message));
    }

    @Override
    public Envelop decodeFromWire(final int i,
                                  final Buffer buffer) {
        return Ut.fromBuffer(i, buffer);
    }

    @Override
    public Envelop transform(final Envelop message) {
        return message;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return VValue.CODECS;
    }
}
