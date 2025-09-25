package io.zerows.core.web.websocket.store;

interface INFO {

    String SCANNED_SOCKS = "( {1} WebSocket ) The endpoint {0} scanned {1} websockets of Event, " +
        "will be mounted to event bus.";


    String SOCK_HIT = "( Socket ) The socket job {0} will be deployed, socket = `{1}`, address = `{2}`.";
}
