package io.zerows.extension.mbse.action.util;

import io.zerows.extension.mbse.action.uca.tunnel.ActorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.AdaptorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.ConnectorChannel;
import io.zerows.extension.mbse.action.uca.tunnel.DirectorChannel;
import io.zerows.platform.enums.EmWeb;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {

    ConcurrentMap<EmWeb.Channel, Class<?>> CHANNELS = new ConcurrentHashMap<>() {
        {
            this.put(EmWeb.Channel.ACTOR, ActorChannel.class);
            this.put(EmWeb.Channel.DIRECTOR, DirectorChannel.class);
            this.put(EmWeb.Channel.ADAPTOR, AdaptorChannel.class);
            this.put(EmWeb.Channel.CONNECTOR, ConnectorChannel.class);
        }
    };
}
