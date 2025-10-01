package io.zerows.extension.mbse.action.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.enums.app.EmTraffic;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.datamation.KDictConfig;
import io.zerows.epoch.common.shared.datamation.KMap;
import io.zerows.epoch.common.shared.normalize.KIdentity;
import io.zerows.core.database.atom.Database;
import io.zerows.epoch.common.log.Log;
import io.zerows.epoch.common.log.LogModule;
import io.zerows.extension.mbse.action.atom.JtConfig;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.atom.JtWorker;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.domain.tables.pojos.IJob;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HRule;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Jt {

    public static String jobCode(final IJob job) {
        return job.getNamespace() + VString.DASH + job.getCode();
    }

    /*
     * Extraction for some specification data
     */
    public static String toPath(final HArk ark, final Supplier<String> uriSupplier,
                                final boolean secure, final JtConfig external) {
        return JtRoute.toPath(ark, uriSupplier, secure, external);
    }

    public static String toPath(final HArk ark, final Supplier<String> uriSupplier,
                                final boolean secure) {
        return JtRoute.toPath(ark, uriSupplier, secure);
    }

    public static Set<MediaType> toMime(final Supplier<String> supplier) {
        return JtRoute.toMime(supplier);
    }

    public static Set<String> toMimeString(final Supplier<String> supplier) {
        return toMime(supplier).stream()
            .map(type -> type.getType() + VString.SLASH + type.getSubtype())
            .collect(Collectors.toSet());
    }

    /*
     * IService -> Dict
     */
    public static KDictConfig toDict(final IService service) {
        return JtBusiness.toDict(service);
    }

    /*
     * IService -> DualMapping
     */
    public static KMap toMapping(final IService service) {
        return JtBusiness.toMapping(service);
    }

    /*
     * IService -> Identify
     */
    public static KIdentity toIdentity(final IService service) {
        return JtBusiness.toIdentify(service);
    }

    public static Future<ConcurrentMap<String, JsonArray>> toDictionary(final String key, final String cacheKey, final String identifier, final KDictConfig dict) {
        return JtBusiness.toDictionary(key, cacheKey, identifier, dict);
    }

    public static Set<String> toSet(final Supplier<String> supplier) {
        return JtRoute.toSet(supplier);
    }

    /*
     * Type extraction
     */
    public static JtWorker toWorker(final IApi api) {
        return JtType.toWorker(api);
    }

    public static Class<?> toChannel(final Supplier<String> supplier, final EmTraffic.Channel type) {
        return JtType.toChannel(supplier, type);
    }

    public static void initApi(final IApi api) {
        JtDataObject.initApi(api);
    }

    public static JsonObject toOptions(final IService service, final IApi api) {
        return JtDataObject.toOptions(service, api);
    }

    public static JsonObject toOptions(final IService service, final IJob job) {
        return JtDataObject.toOptions(service, job);
    }

    public static JsonObject toOptions(final IService service) {
        return JtDataObject.toOptions(service);
    }

    /*
     * Ask configuration, before deployVerticle here
     * 1. JtUri -> JsonObject
     * 2. Set -> Map ( key -> JtUri -> JsonObject )
     * 3. Before deployment of Verticle
     */
    public static ConcurrentMap<String, JsonObject> ask(final Set<JtUri> uriSet) {
        return JtDelivery.ask(uriSet);
    }

    /*
     * Answer configuration, after deployVerticle here
     * 1. JsonObject -> JtUri
     * 2. Map ( key -> apiKey -> JsonObject -> JtUri )
     * 3. After deployment of Verticle ( Consume )
     */
    public static ConcurrentMap<String, JtUri> answer(final JsonObject config) {
        return JtDelivery.answer(config);
    }

    /**
     * 双通道模式下的数据库
     *
     * @param service 通道服务接口
     *
     * @return 选择的数据库
     */
    public static Database toDatabase(final IService service) {
        return JtDataObject.toDatabase(service);
    }

    public static HRule toRule(final IService service) {
        return JtDataObject.toRule(service);
    }

    public static KIntegration toIntegration(final IService service) {
        return JtDataObject.toIntegration(service);
    }

    public interface LOG {
        String MODULE = "Πίδακας δρομολογητή";

        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Route = Log.modulat(MODULE).extension("Route");
        LogModule Worker = Log.modulat(MODULE).extension("Worker");
        LogModule Web = Log.modulat(MODULE).extension("Web");
        LogModule App = Log.modulat(MODULE).extension("App");
    }
}
