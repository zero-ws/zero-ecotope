package io.zerows.extension.mbse.action.util;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.support.Ut;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.platform.enums.EmAop;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KIdentity;
import io.zerows.platform.metadata.KMap;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/*
 * All dict / identity / dualMapping
 * have been put into pool structure
 */
class JtBusiness {
    private static final Cc<String, KDictConfig> CC_DICT = Cc.open();
    private static final Cc<String, KMap> CC_MAPPING = Cc.open();
    private static final Cc<String, KIdentity> CC_IDENTITY = Cc.open();

    static KDictConfig toDict(final IService service) {
        return Fn.jvmOr(() -> CC_DICT.pick(() -> {
            /*
             * Dict Config for service
             */
            final String dictStr = service.getDictConfig();
            final KDictConfig dict = new KDictConfig(dictStr);
            /*
             * When valid, inject component here
             */
            if (!dict.configSource().isEmpty()) {
                final Class<?> component =
                    Ut.clazz(service.getDictComponent(), null);
                dict.bind(component);
                /*
                 * dictEpsilon configuration
                 */
                final JsonObject epsilonJson = Ut.toJObject(service.getDictEpsilon());
                dict.bind(Ux.dictUse(epsilonJson));
            }
            /*
             * If Dict is not required, means
             * 1) The component could not be found
             * 2) The Dict Source configured list is empty, it's not needed
             */
            return dict;
        }));
    }

    static KMap toMapping(final IService service) {
        return Fn.jvmOr(() -> CC_MAPPING.pick(() -> {
            /*
             * DualMapping
             */
            final EmAop.Effect mode = Ut.toEnum(service::getMappingMode, EmAop.Effect.class, EmAop.Effect.NONE);
            final KMap mapping = new KMap();
            /*
             * The mode != NONE means that there must contain configuration
             */
            final JsonObject config = Ut.toJObject(service.getMappingConfig());
            /*
             * 「Optional」The component inject
             */
            final Class<?> component = Ut.clazz(service.getMappingComponent(), null);
            mapping.init(config).bind(mode).bind(component);
            return mapping;
        }));
    }

    static KIdentity toIdentify(final IService service) {
        return Fn.jvmOr(() -> CC_IDENTITY.pick(() -> {
            /*
             * KIdentity for `identifier` processing
             */
            final KIdentity identity = new KIdentity();
            identity.setIdentifier(service.getIdentifier());
            final Class<?> component = Ut.clazz(service.getIdentifierComponent(), null);
            identity.setIdentifierComponent(component);
            /*
             * Bind sigma to identity for future usage.
             */
            identity.setSigma(service.getSigma());
            return identity;
        }));
    }

    static Future<ConcurrentMap<String, JsonArray>> toDictionary(final String key, final String cacheKey, final String identifier, final KDictConfig dict) {
        /*
         * Params here for different situations
         */
        final MultiMap paramMap = MultiMap.caseInsensitiveMultiMap();
        paramMap.add(KName.IDENTIFIER, identifier);
        paramMap.add(KName.CACHE_KEY, cacheKey);
        final HArk ark = Ke.ark(key);
        if (Objects.nonNull(ark)) {
            final HApp app = ark.app();
            final String sigma = app.option(KName.SIGMA);
            paramMap.add(KName.SIGMA, sigma);
            final String appId = app.option(KName.APP_ID);
            paramMap.add(KName.APP_ID, appId);
        }
        return Ux.dictData(dict, paramMap);

    }
}
