package io.zerows.extension.mbse.action.util;

import io.vertx.core.AbstractVerticle;
import io.zerows.ams.constant.em.app.EmTraffic;
import io.zerows.core.fn.RFn;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.action.atom.JtWorker;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.eon.JtConstant;
import io.zerows.extension.mbse.action.eon.em.WorkerType;
import io.zerows.extension.mbse.action.exception._500ConsumerSpecException;
import io.zerows.extension.mbse.action.exception._500WorkerSpecException;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtConsumer;
import io.zerows.extension.mbse.action.uca.tunnel.AdaptorChannel;

import java.util.function.Supplier;

class JtType {

    private static Class<?> toWorker(final Supplier<String> supplier) {
        final String workerStr = supplier.get();
        final Class<?> clazz = Ut.clazz(workerStr, JtConstant.COMPONENT_DEFAULT_WORKER);
        RFn.out(AbstractVerticle.class != clazz.getSuperclass(), _500WorkerSpecException.class, JtRoute.class, clazz);
        return clazz;
    }

    private static Class<?> toConsumer(final Supplier<String> supplier) {
        final String consumerStr = supplier.get();
        final Class<?> clazz = Ut.clazz(consumerStr, JtConstant.COMPONENT_DEFAULT_CONSUMER);
        RFn.out(!Ut.isImplement(clazz, JtConsumer.class), _500ConsumerSpecException.class, JtRoute.class, clazz);
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

    static Class<?> toChannel(final Supplier<String> supplier, final EmTraffic.Channel type) {
        final Class<?> clazz;
        if (EmTraffic.Channel.DEFINE == type) {
            /*
             * User defined channel class
             *  */
            final String channelClass = supplier.get();
            if (Ut.isNil(channelClass)) {
                /*
                 * Adaptor as default channel
                 *  */
                clazz = Pool.CHANNELS.get(EmTraffic.Channel.ADAPTOR);
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
            clazz = Pool.CHANNELS.getOrDefault(type, AdaptorChannel.class);
        }
        return clazz;
    }
}
