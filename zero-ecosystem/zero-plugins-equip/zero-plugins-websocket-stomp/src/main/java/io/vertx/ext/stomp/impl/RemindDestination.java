package io.vertx.ext.stomp.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.stomp.*;
import io.vertx.ext.stomp.utils.Headers;
import io.zerows.component.log.Annal;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.web.websocket.router.SockGrid;
import io.zerows.plugins.websocket.stomp.handler.StompBridgeOptions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy from EventBusBridge, here we'll modify some code logical and involve
 * the @Subscribe proxy method instead, in this kind of situation the default
 * operation will be modified
 *
 * I'm not sure whether the default implementation is a bug because the
 * InBound, OutBound configure have issues.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RemindDestination extends Topic {
    private static final Annal LOGGER = Annal.get(RemindDestination.class);

    private final BridgeOptions options;

    private final Map<String, Pattern> expressions = new HashMap<>();

    private final Map<String, MessageConsumer<?>> registry = new HashMap<>();


    public RemindDestination(final Vertx vertx) {
        super(vertx, null);
        this.options = StompBridgeOptions.wsOptionBridge();
    }

    @Override
    public String destination() {
        return "<<bridge>>";
    }

    @Override
    public synchronized Destination subscribe(final StompServerConnection connection, final Frame frame) {
        final String address = frame.getDestination();
        /*
         * Need to check whether the client can receive message from the event bus (outbound).
         * Here the destination address is subscribe address
         * 1) InBound = @Address to EventBus
         * 2) OutBound = @Subscribe to WebSocket
         *
         * Here we'll split the address of Subscribe & EventBus to implement user-defined
         * @Subscribe("xxxx") @Address("yyyy")
         */
        String addressEvent = SockGrid.configAddress(address);
        if (Ut.isNil(addressEvent)) {
            // The Map does not get the address
            addressEvent = address;
        }
        if (this.checkMatches(false, address, null)) {
            // We need the subscription object to transform messages.
            final Subscription subscription = new Subscription(connection, frame);
            this.subscriptions.add(subscription);
            if (!this.registry.containsKey(address)) {
                this.registry.put(address, this.vertx.eventBus().consumer(addressEvent, msg -> {
                    if (!this.checkMatches(false, address, msg.body())) {
                        return;
                    }

                    /* Code modified here for inject method calling */
                    SockGrid.wsInvoke(address, msg.body(), (returned) -> {
                        final Object resultData = returned.result();
                        if (Objects.isNull(resultData)) {
                            LOGGER.warn("[ Warning ] The invoker returned value is null, the message will be ignored.");
                            return;
                        }
                        final Object invoked = returned.result();
                        if (this.options.isPointToPoint()) {
                            final Optional<Subscription> chosen = this.subscriptions.stream().filter(s -> s.destination.equals(address)).findAny();
                            if (chosen.isPresent()) {
                                final Subscription target = chosen.get();
                                final FrameBuilder builder = FrameBuilder.of(target.id);
                                final Frame stompFrame = builder.buildFrame(msg, invoked, target.ackMode);
                                chosen.get().connection.write(stompFrame);
                            }
                        } else {
                            this.subscriptions.stream().filter(s -> s.destination.equals(address)).forEach(target -> {
                                final FrameBuilder builder = FrameBuilder.of(target.id);
                                final Frame stompFrame = builder.buildFrame(msg, invoked, target.ackMode);
                                target.connection.write(stompFrame);
                            });
                        }
                    });
                }));
            }
            return this;
        }
        return null;
    }

    @Override
    public synchronized boolean unsubscribe(final StompServerConnection connection, final Frame frame) {
        for (final Subscription subscription : new ArrayList<>(this.subscriptions)) {
            if (subscription.connection.equals(connection)
                && subscription.id.equals(frame.getId())) {

                final boolean r = this.subscriptions.remove(subscription);
                this.unsubscribe(subscription);
                return r;
            }
        }
        return false;
    }

    @Override
    public synchronized Destination unsubscribeConnection(final StompServerConnection connection) {
        new ArrayList<>(this.subscriptions)
            .stream()
            .filter(subscription -> subscription.connection.equals(connection))
            .forEach(s -> {
                this.subscriptions.remove(s);
                this.unsubscribe(s);
            });
        return this;
    }

    private void unsubscribe(final Subscription s) {
        final Optional<Subscription> any = this.subscriptions.stream().filter(s2 -> s2.destination.equals(s.destination))
            .findAny();
        // We unregister the event bus consumer if there are no subscription on this address anymore.
        if (any.isEmpty()) {
            final MessageConsumer<?> consumer = this.registry.remove(s.destination);
            if (consumer != null) {
                consumer.unregister();
            }
        }
    }

    @Override
    public Destination dispatch(final StompServerConnection connection, final Frame frame) {
        final String address = frame.getDestination();
        // Send a frame to the event bus, check if this inbound traffic is allowed.
        if (this.checkMatches(true, address, frame.getBody())) {
            final String replyAddress = frame.getHeader("reply-address");
            if (replyAddress != null) {
                this.send(address, frame, (final AsyncResult<Message<Object>> res) -> {
                    if (res.failed()) {
                        final Throwable cause = res.cause();
                        connection.write(Frames.createErrorFrame("Message dispatch error", Headers.create(Frame.DESTINATION,
                            address, "reply-address", replyAddress), cause.getMessage())).close();
                    } else {
                        // We are in a request-response interaction, only one STOMP client must receive the message (the one
                        // having sent the given frame).
                        // We look for the subscription with registered to the 'reply-to' destination. It must be unique.
                        final Optional<Subscription> subscription = this.subscriptions.stream()
                            .filter(s -> s.connection.equals(connection) && s.destination.equals(replyAddress))
                            .findFirst();
                        subscription.ifPresent(value -> SockGrid.wsInvoke(address, res.result(), (returned) -> {
                            final FrameBuilder builder = FrameBuilder.of(value.id);
                            final Frame stompFrame = builder.buildFrame(res.result(), returned.result(), value.ackMode);
                            value.connection.write(stompFrame);
                        }));
                    }
                });
            } else {
                this.send(address, frame, null);
            }
        } else {
            connection.write(Frames.createErrorFrame("Access denied", Headers.create(Frame.DESTINATION,
                address), "Access denied to " + address)).close();
            return null;
        }
        return this;
    }

    private void send(final String address, final Frame frame, final Handler<AsyncResult<Message<Object>>> replyHandler) {
        // Event Bus Address seeking
        String addressEvent = SockGrid.configAddress(address);
        if (Ut.isNil(addressEvent)) {
            // The Map dose not get the address
            addressEvent = address;
        }
        if (this.options.isPointToPoint()) {

            this.vertx.eventBus().request(addressEvent, frame.getBody(),
                new DeliveryOptions().setHeaders(this.toMultimap(frame.getHeaders()))).onComplete(replyHandler);
        } else {
            // the reply handler is ignored in non point to point interaction.
            this.vertx.eventBus().publish(addressEvent, frame.getBody(),
                new DeliveryOptions().setHeaders(this.toMultimap(frame.getHeaders())));
        }
    }

    private MultiMap toMultimap(final Map<String, String> headers) {
        return MultiMap.caseInsensitiveMultiMap().addAll(headers);
    }

    @Override
    public boolean matches(final String address) {
        return this.checkMatches(false, address, null) || this.checkMatches(true, address, null);
    }

    private boolean regexMatches(final String matchRegex, final String address) {
        Pattern pattern = this.expressions.get(matchRegex);
        if (pattern == null) {
            pattern = Pattern.compile(matchRegex);
            this.expressions.put(matchRegex, pattern);
        }
        final Matcher m = pattern.matcher(address);
        return m.matches();
    }

    private boolean checkMatches(final boolean inbound, final String address, final Object body) {

        final List<PermittedOptions> matches = inbound ? this.options.getInboundPermitteds() : this.options.getOutboundPermitteds();

        for (final PermittedOptions matchHolder : matches) {
            final String matchAddress = matchHolder.getAddress();
            final String matchRegex;
            if (matchAddress == null) {
                matchRegex = matchHolder.getAddressRegex();
            } else {
                matchRegex = null;
            }

            final boolean addressOK;
            if (matchAddress == null) {
                addressOK = matchRegex == null || this.regexMatches(matchRegex, address);
            } else {
                addressOK = matchAddress.equals(address);
            }

            if (addressOK) {
                return this.structureMatches(matchHolder.getMatch(), body);
            }
        }

        return false;
    }

    private boolean structureMatches(final JsonObject match, final Object body) {
        if (match == null || body == null) {
            return true;
        }

        // Can send message other than JSON too - in which case we can't do deep matching on structure of message

        try {
            final JsonObject object;
            if (body instanceof JsonObject) {
                object = (JsonObject) body;
            } else if (body instanceof Buffer) {
                object = new JsonObject(((Buffer) body).toString("UTF-8"));
            } else if (body instanceof String) {
                object = new JsonObject((String) body);
            } else {
                return false;
            }

            for (final String fieldName : match.fieldNames()) {
                final Object mv = match.getValue(fieldName);
                final Object bv = object.getValue(fieldName);
                // Support deep matching
                if (mv instanceof JsonObject) {
                    if (!this.structureMatches((JsonObject) mv, bv)) {
                        return false;
                    }
                } else if (!match.getValue(fieldName).equals(object.getValue(fieldName))) {
                    return false;
                }
            }
            return true;
        } catch (final Exception e) {
            // Was not a valid json object refuse the message
            return false;
        }
    }
}
