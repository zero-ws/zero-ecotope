package io.zerows.extension.mbse.action.util;

import io.zerows.platform.enums.app.EmTraffic;
import io.zerows.extension.mbse.action.uca.tunnel.ActorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.AdaptorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.ConnectorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.DirectorChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {

    ConcurrentMap<EmTraffic.Channel, Class<?>> CHANNELS = new ConcurrentHashMap<>() {
        {
            this.put(EmTraffic.Channel.ACTOR, ActorChannel.class);
            this.put(EmTraffic.Channel.DIRECTOR, DirectorChannel.class);
            this.put(EmTraffic.Channel.ADAPTOR, AdaptorChannel.class);
            this.put(EmTraffic.Channel.CONNECTOR, ConnectorChannel.class);
        }
    };
}
