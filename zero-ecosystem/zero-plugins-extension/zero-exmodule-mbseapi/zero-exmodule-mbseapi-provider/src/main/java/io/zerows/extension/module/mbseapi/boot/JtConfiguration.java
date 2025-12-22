package io.zerows.extension.module.mbseapi.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.extension.module.mbseapi.metadata.JtConfigOld;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.skeleton.common.KeMsg;
import io.zerows.management.OZeroStore;
import io.zerows.program.Ux;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-07
 */
class JtConfiguration {
    private static final ConcurrentMap<String, ServiceEnvironment> CONTEXT =
        new ConcurrentHashMap<>();
    private static JtConfigOld CONFIG = null;

    static void registry(final HAmbient ambient) {
        if (Objects.isNull(CONFIG) && OZeroStore.is(YmlCore.router.__KEY)) {
            final JsonObject routerData = OZeroStore.option(YmlCore.router.__KEY);
            final String module = JtConstant.BUNDLE_SYMBOLIC_NAME; // Ke.getExtension(KeIpc.Module.JET);
            Jt.LOG.Init.info(JtConfiguration.class, KeMsg.Configuration.DATA_J,
                module, routerData.encode());

            ambient.registry(module, routerData);

            CONFIG = Ut.deserialize(routerData, JtConfigOld.class);
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

    static JtConfigOld getConfig() {
        return CONFIG;
    }

    static ConcurrentMap<String, ServiceEnvironment> serviceEnvironment() {
        return CONTEXT;
    }
}
