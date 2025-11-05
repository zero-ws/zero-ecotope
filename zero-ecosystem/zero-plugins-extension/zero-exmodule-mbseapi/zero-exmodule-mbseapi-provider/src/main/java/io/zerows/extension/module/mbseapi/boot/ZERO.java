package io.zerows.extension.module.mbseapi.boot;

import io.zerows.extension.module.mbseapi.component.JtChannelActor;
import io.zerows.extension.module.mbseapi.component.JtChannelAdaptor;
import io.zerows.extension.module.mbseapi.component.JtChannelConnector;
import io.zerows.extension.module.mbseapi.component.JtChannelDirector;
import io.zerows.platform.enums.EmWeb;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {

    ConcurrentMap<EmWeb.Channel, Class<?>> CHANNELS = new ConcurrentHashMap<>() {
        {
            this.put(EmWeb.Channel.ACTOR, JtChannelActor.class);
            this.put(EmWeb.Channel.DIRECTOR, JtChannelDirector.class);
            this.put(EmWeb.Channel.ADAPTOR, JtChannelAdaptor.class);
            this.put(EmWeb.Channel.CONNECTOR, JtChannelConnector.class);
        }
    };
}
