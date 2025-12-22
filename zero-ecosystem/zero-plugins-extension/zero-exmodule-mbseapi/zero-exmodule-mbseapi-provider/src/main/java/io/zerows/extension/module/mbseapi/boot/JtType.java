package io.zerows.extension.module.mbseapi.boot;

import io.r2mo.function.Fn;
import io.vertx.core.AbstractVerticle;
import io.zerows.extension.module.mbseapi.common.em.WorkerType;
import io.zerows.extension.module.mbseapi.component.JtChannelActor;
import io.zerows.extension.module.mbseapi.component.JtChannelAdaptor;
import io.zerows.extension.module.mbseapi.component.JtChannelConnector;
import io.zerows.extension.module.mbseapi.component.JtChannelDirector;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IApi;
import io.zerows.extension.module.mbseapi.exception._80404Exception500WorkerSpec;
import io.zerows.extension.module.mbseapi.exception._80405Exception500ConsumerSpec;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.module.mbseapi.metadata.JtWorker;
import io.zerows.extension.module.mbseapi.plugins.JtConsumer;
import io.zerows.platform.enums.EmWeb;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

class JtType {

    public static final ConcurrentMap<EmWeb.Channel, Class<?>> CHANNELS = new ConcurrentHashMap<>() {
        {
            this.put(EmWeb.Channel.ACTOR, JtChannelActor.class);
            this.put(EmWeb.Channel.DIRECTOR, JtChannelDirector.class);
            this.put(EmWeb.Channel.ADAPTOR, JtChannelAdaptor.class);
            this.put(EmWeb.Channel.CONNECTOR, JtChannelConnector.class);
        }
    };

    private static Class<?> toWorker(final Supplier<String> supplier) {
        final String workerStr = supplier.get();
        final Class<?> clazz = Ut.clazz(workerStr, JtConstant.COMPONENT_DEFAULT_WORKER);
        Fn.jvmKo(!AbstractVerticle.class.isAssignableFrom(clazz.getSuperclass()), _80404Exception500WorkerSpec.class, clazz);
        return clazz;
    }

    private static Class<?> toConsumer(final Supplier<String> supplier) {
        final String consumerStr = supplier.get();
        final Class<?> clazz = Ut.clazz(consumerStr, JtConstant.COMPONENT_DEFAULT_CONSUMER);
        Fn.jvmKo(!Ut.isImplement(clazz, JtConsumer.class), _80405Exception500ConsumerSpec.class, clazz);
        return clazz;
    }


    static JtWorker toWorker(final IApi api) {
        final JtWorker worker = new JtWorker();

        /*
         * Worker object instance in current uri here.
         */
        worker.setWorkerAddress(api.getWorkerAddress());
        worker.setWorkerJs(api.getWorkerJs());
        worker.setWorkerType(Ut.toEnum(api::getWorkerType, WorkerType.class, WorkerType.STD));
        worker.setWorkerClass(toWorker(api::getWorkerClass));
        worker.setWorkerConsumer(JtType.toConsumer(api::getWorkerConsumer));
        return worker;
    }

    static Class<?> toChannel(final Supplier<String> supplier, final EmWeb.Channel type) {
        final Class<?> clazz;
        if (EmWeb.Channel.DEFINE == type) {
            /*
             * User defined channel class
             *  */
            final String channelClass = supplier.get();
            if (Ut.isNil(channelClass)) {
                /*
                 * Adaptor as default channel
                 *  */
                clazz = CHANNELS.get(EmWeb.Channel.ADAPTOR);
            } else {
                /*
                 * User defined channel as selected channel
                 *  */
                clazz = Ut.clazz(channelClass);
            }
        } else {
            /*
             * Here the type should be not "DEFINE", it used `Standard` channel
             * */
            clazz = CHANNELS.getOrDefault(type, JtChannelAdaptor.class);
        }
        return clazz;
    }
}
