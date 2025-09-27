package io.zerows.extension.runtime.workflow.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.database.atom.Database;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.extension.runtime.workflow.atom.configuration.MetaWorkflow;
import io.zerows.extension.runtime.workflow.domain.tables.daos.WFlowDao;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WFlow;
import io.zerows.extension.runtime.workflow.eon.WfConstant;
import io.zerows.module.metadata.store.OZeroStore;
import io.zerows.specification.access.app.HAmbient;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;
import org.jooq.Configuration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
final class WfConfiguration {
    private static final ConcurrentMap<String, WFlow> FLOW_POOL = new ConcurrentHashMap<>();
    private static MetaWorkflow CONFIG;
    private static ProcessEngine ENGINE;
    private static HistoryEventHandler HANDLER;

    private WfConfiguration() {
    }

    private static MetaWorkflow configure() {
        if (Objects.isNull(CONFIG) && OZeroStore.is(YmlCore.workflow.__KEY)) {
            CONFIG = OZeroStore.option(YmlCore.workflow.__KEY, MetaWorkflow.class, null);
            LOG.Init.info(WfConfiguration.class, KeMsg.Configuration.DATA_T, CONFIG.toString());
        }
        return CONFIG;
    }

    static Future<Boolean> registry(final HAmbient ambient, final Vertx vertx) {
        final JsonObject configJ = OZeroStore.option(YmlCore.workflow.__KEY);
        final String module = WfConstant.BUNDLE_SYMBOLIC_NAME;
        LOG.Init.info(WfConfiguration.class, KeMsg.Configuration.DATA_J,
            module, configJ.encode());

        ambient.registry(module, configJ);

        configure();

        final Configuration configuration = Ke.getConfiguration();
        final WFlowDao flowDao = new WFlowDao(configuration, vertx);
        return flowDao.findAll().compose(flows -> {
            LOG.Init.info(WfConfiguration.class, "Flow definitions: {0}", flows.size());
            FLOW_POOL.putAll(Ut.elementZip(flows, WFlow::getCode, flow -> flow));
            return Ux.futureT();
        });
    }

    /*
     * Camunda Engine Creating
     */
    static ProcessEngine camunda() {
        Objects.requireNonNull(CONFIG);
        if (Objects.isNull(ENGINE)) {
            final Database database = CONFIG.camundaDatabase();
            Objects.requireNonNull(database);
            final ProcessEngineConfigurationImpl configuration = new StandaloneProcessEngineConfiguration()
                // Fix Issue:
                // org.camunda.bpm.engine.ProcessEngineException: historyLevel mismatch: configuration says HistoryLevelAudit(name=audit, id=2) and database says HistoryLevelFull(name=full, id=3)
                .setHistory(HistoryLevel.HISTORY_LEVEL_FULL.getName())     // none, audit, full, activity
                .setHistoryEventHandler(new DbHistoryEventHandler())
                // Fix Issue:
                // ENGINE-12019 The transaction isolation level set for the database is 'REPEATABLE_READ' which differs
                // from the recommended value. Please change the isolation level to 'READ_COMMITTED' or set property
                // 'skipIsolationLevelCheck' to true. Please keep in mind that some levels are known to cause deadlocks
                // and other unexpected behaviours.
                .setSkipIsolationLevelCheck(true)
                .setIdGenerator(new StrongUuidGenerator())                 // uuid for task
                .setProcessEngineName(CONFIG.getName())
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
                .setJdbcUrl(database.getJdbcUrl())
                .setJdbcDriver(database.getDriverClassName())
                .setJdbcUsername(database.getUsername())
                .setJdbcPassword(database.getSmartPassword())
                .setJobExecutorActivate(true);
            // Default Handler for History
            HANDLER = configuration.getHistoryEventHandler();
            ENGINE = configuration.buildProcessEngine();
        }
        return ENGINE;
    }

    static Set<String> camundaBuiltIn() {
        final MetaWorkflow configRef = configure();
        return Optional.ofNullable(configRef)
            .map(MetaWorkflow::camundaBuiltIn)
            .orElseGet(HashSet::new);
    }

    static HistoryEventHandler camundaLogger() {
        return HANDLER;
    }

    static WFlow workflow(final String code) {
        return FLOW_POOL.get(code);
    }
}
