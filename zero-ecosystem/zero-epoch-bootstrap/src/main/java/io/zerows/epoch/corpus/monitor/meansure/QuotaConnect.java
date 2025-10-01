package io.zerows.epoch.corpus.monitor.meansure;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.store.OZeroStore;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class QuotaConnect {
    private static final boolean IS_MONITOR;
    private static final OLog LOGGER = Ut.Log.uca(QuotaConnect.class);
    /*
     * Default Quota that will be mounted to health monitor
     *
     * - session: SessionMonitor
     * - instance: Verticle Monitor
     */
    static ConcurrentMap<String, Function<Vertx, Quota>> REGISTRY_CLS = new ConcurrentHashMap<>();
    private static String PATH;

    static {
        if (OZeroStore.is("monitor")) {
            IS_MONITOR = true;
            REGISTRY_CLS.put("session", SessionQuota::new);
            REGISTRY_CLS.put("instance", VerticleQuota::new);
            final JsonObject monitorJ = OZeroStore.option("monitor");
            final String secure = Ut.valueString(monitorJ, "secure");
            if (Ut.isNil(secure)) {
                PATH = KWeb.ADDR.API_MONITOR;
            } else {
                PATH = secure + KWeb.ADDR.API_MONITOR;
            }
            final JsonArray quotas = Ut.valueJArray(monitorJ, "quota");

            /*
             * Mount to REGISTRY_CLS
             */
            if (!quotas.isEmpty()) {
                LOGGER.info("[ Hc ] Configured size: {0}, root path: {1}",
                    String.valueOf(quotas.size()), PATH);
                final StringBuilder message = new StringBuilder("[ Hc ] Initialize components: ");
                Ut.itJArray(quotas).forEach(item -> {
                    final String path = item.getString("path", null);
                    final String componentName = item.getString("component");
                    if (Ut.isNotNil(path) && !REGISTRY_CLS.containsKey(path)) {
                        final Class<?> componentCls = Ut.clazz(componentName, null);
                        if (Objects.nonNull(componentCls)) {
                            REGISTRY_CLS.put(path, (vertx) -> Ut.instance(componentCls, vertx));
                            message.append(MessageFormat.format("\n\t{0} = {1}", path,
                                componentCls.getName()));
                        }
                    }
                });
                message.append("\n");
                LOGGER.info(message.toString());
            }
        } else {
            IS_MONITOR = false;
        }
    }

    public static String routePath() {
        return PATH;
    }

    public static boolean monitor() {
        return IS_MONITOR;
    }
}
