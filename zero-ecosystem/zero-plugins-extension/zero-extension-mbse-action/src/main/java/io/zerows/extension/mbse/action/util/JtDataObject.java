package io.zerows.extension.mbse.action.util;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.app.KDS;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.datamation.KDictConfig;
import io.zerows.core.constant.KName;
import io.zerows.core.database.atom.Database;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.domain.tables.pojos.IJob;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.extension.mbse.action.eon.JtConstant;
import io.zerows.extension.mbse.action.eon.em.WorkerType;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HRule;

import java.util.Objects;
import java.util.Optional;

class JtDataObject {

    static KIntegration toIntegration(final IService service) {
        if (Objects.isNull(service)) {
            return new KIntegration();
        } else {
            final JsonObject data = Ut.toJObject(service.getConfigIntegration());
            final KIntegration integration = new KIntegration();
            integration.fromJson(data);
            // Dict
            final KDictConfig dict = JtBusiness.toDict(service);
            if (Objects.nonNull(dict) && !dict.configUse().isEmpty()) {
                /*
                 * Internal binding
                 */
                integration.setEpsilon(dict.configUse());
            }
            /*
             * SSL Options
             */
            // TODO: SSL Options
            return integration;
        }
    }

    static HRule toRule(final IService service) {
        if (Objects.isNull(service)) {
            return null;
        } else {
            final String rules = service.getRuleUnique();
            if (Ut.isNil(rules)) {
                return null;
            } else {
                return HRule.of(rules);
            }
        }
    }

    static Database toDatabase(final IService service) {
        final HArk ark = Ke.ark(service.getSigma());
        final KDS<Database> ds = ark.database();
        final JsonObject databaseJ = Ut.toJObject(service.getConfigDatabase());
        // 通道中未配置数据库
        if (Ut.isNil(databaseJ)) {
            return ds.dynamic();
        }
        // 构造通道中数据库
        final Database database = new Database();
        database.fromJson(databaseJ);
        return database;
    }

    @SuppressWarnings("all")
    static JsonObject toOptions(final IService service, final IApi api) {
        final JsonObject options = toOptions(service);
        // TODO: Api configuration
        return options;
    }

    @SuppressWarnings("all")
    static JsonObject toOptions(final IService service, final IJob job) {
        final JsonObject options = toOptions(service);
        // TODO: Job configuration
        return options;
    }

    static JsonObject toOptions(final IService service) {
        final HArk ark = Ke.ark(service.getSigma());
        /*
         * SERVICE_CONFIG / serviceComponent options
         * here for configuration instead of others
         * {
         *    "name": appName,
         *    "identifier": <id>,
         *    "sigma": <sigma>
         * }
         */
        final JsonObject options = Ut.toJObject(service.getServiceConfig());
        {
            final HApp app = ark.app();
            /* default options, you can add more */
            options.put(KName.NAME, app.name());
            final String sigma = app.option(KName.SIGMA);
            Optional.ofNullable(sigma).ifPresent(value -> options.put(KName.SIGMA, value));
            options.put(KName.IDENTIFIER, service.getIdentifier());
        }
        return options;
    }

    static void initApi(final IApi api) {
        /*
         * Set default value in I_API related to worker
         * workerType
         * workerAddress
         * workerConsumer
         * workerClass
         * workerJs
         */
        if (Ut.isNil(api.getWorkerClass())) {
            api.setWorkerClass(JtConstant.COMPONENT_DEFAULT_WORKER.getName());
        }
        if (Ut.isNil(api.getWorkerAddress())) {
            api.setWorkerAddress(JtConstant.EVENT_ADDRESS);
        }
        if (Ut.isNil(api.getWorkerConsumer())) {
            api.setWorkerConsumer(JtConstant.COMPONENT_DEFAULT_CONSUMER.getName());
        }
        if (Ut.isNil(api.getWorkerType())) {
            api.setWorkerType(WorkerType.STD.name());
        }
    }
}
