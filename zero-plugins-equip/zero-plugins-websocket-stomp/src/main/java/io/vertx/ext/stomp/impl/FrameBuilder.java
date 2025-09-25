package io.vertx.ext.stomp.impl;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.Command;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.StompOptions;
import io.vertx.ext.stomp.utils.Headers;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2024-04-04
 */
class FrameBuilder {
    private static final Cc<String, FrameBuilder> CC_SUBSCRIPTION = Cc.open();
    private final String subscriptionId;

    private FrameBuilder(final String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    static FrameBuilder of(final String subscriptionId) {
        return CC_SUBSCRIPTION.pick(() -> new FrameBuilder(subscriptionId), subscriptionId);
    }

    Frame buildFrame(final Message<Object> msg,
                     final Object bodyData, final String ackMode) {
        final String messageId = UUID.randomUUID().toString();

        final Frame frame = new Frame();
        frame.setCommand(Command.MESSAGE);

        final Headers headers = Headers.create(frame.getHeaders())
            // Destination already set in the input headers.
            .add(Frame.SUBSCRIPTION, this.subscriptionId)
            .add(Frame.MESSAGE_ID, messageId)
            .add(Frame.DESTINATION, msg.address())
            .add(Frame.CONTENT_TYPE, StompOptions.UTF_8);
        if (!"auto".equals(ackMode)) {
            // We reuse the message Id as ack Id
            headers.add(Frame.ACK, messageId);
        }

        // Specific headers.
        if (msg.replyAddress() != null) {
            headers.put("reply-address", msg.replyAddress());
        }

        // Copy headers.
        for (final Map.Entry<String, String> entry : msg.headers()) {
            headers.putIfAbsent(entry.getKey(), entry.getValue());
        }

        frame.setHeaders(headers);

        final Object body = Objects.nonNull(bodyData) ? bodyData : msg.body();
        if (body != null) {
            if (body instanceof Buffer) {
                frame.setBody((Buffer) body);
            } else {
                final String content;
                if (body instanceof final JsonObject bodyJ) {
                    content = bodyJ.encode();
                } else if (body instanceof final JsonArray bodyA) {
                    content = bodyA.encode();
                } else {
                    content = body.toString();
                }
                final Buffer bodyResponse = Buffer.buffer(content);
                frame.setBody(bodyResponse);
            }
        }

        if (body != null && frame.getHeader(Frame.CONTENT_LENGTH) == null) {
            frame.addHeader(Frame.CONTENT_LENGTH, Integer.toString(frame.getBody().length()));
        }

        return frame;
    }
}
