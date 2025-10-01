package io.zerows.extension.mbse.action.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.metadata.store.OZeroStore;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.extension.mbse.action.atom.JtConfig;
import io.zerows.extension.mbse.action.eon.JtConstant;
import io.zerows.extension.mbse.action.util.Jt;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-07
 */
class JtConfiguration {
    private static final ConcurrentMap<String, ServiceEnvironment> CONTEXT =
        new ConcurrentHashMap<>();
    private static JtConfig CONFIG = null;

    static void registry(final HAmbient ambient) {
        if (Objects.isNull(CONFIG) && OZeroStore.is(YmlCore.router.__KEY)) {
            final JsonObject routerData = OZeroStore.option(YmlCore.router.__KEY);
            final String module = JtConstant.BUNDLE_SYMBOLIC_NAME; // Ke.getExtension(KeIpc.Module.JET);
            Jt.LOG.Init.info(JtConfiguration.class, KeMsg.Configuration.DATA_J,
                module, routerData.encode());

            ambient.registry(module, routerData);

            CONFIG = Ut.deserialize(routerData, JtConfig.class);
            Jt.LOG.Init.info(JtConfiguration.class, KeMsg.Configuration.DATA_T,
                CONFIG.toString());
            Jt.LOG.Init.info(JtConfiguration.class, "---> Jt @Wall for `{0}`", CONFIG.getWall());
        }
    }

    static Future<Boolean> init(final Vertx vertx, final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();

        Jt.LOG.Init.info(JtConfiguration.class, "HAmbient detect {0} applications in your environment.",
            String.valueOf(stored.size()));
        if (stored.isEmpty()) {
            Jt.LOG.App.warn(JtConfiguration.class, "HAmbient environment pool is Empty.");
        }

        final ConcurrentMap<String, Future<ServiceEnvironment>> futures = new ConcurrentHashMap<>();
        stored.forEach((appId, each) ->
            futures.put(appId, new ServiceEnvironment(each).init(vertx)));
        return Fx.combineM(futures).compose(processed -> {
            CONTEXT.putAll(processed);
            Jt.LOG.Init.info(JtConfiguration.class, "ServiceEnvironment initialized !!!");
            return Ux.future(Boolean.TRUE);
        });
    }

    static JtConfig getConfig() {
        return CONFIG;
    }

    static ConcurrentMap<String, ServiceEnvironment> serviceEnvironment() {
        return CONTEXT;
    }
}
