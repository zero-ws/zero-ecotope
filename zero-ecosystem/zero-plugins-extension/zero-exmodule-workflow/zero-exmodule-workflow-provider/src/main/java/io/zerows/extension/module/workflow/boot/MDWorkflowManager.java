package io.zerows.extension.module.workflow.boot;

import io.r2mo.base.dbe.Database;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.domain.tables.daos.WFlowDao;
import io.zerows.extension.module.workflow.domain.tables.pojos.WFlow;
import io.zerows.extension.module.workflow.metadata.MetaWorkflow;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDWorkflowManager extends MDModuleManager<MetaWorkflow, Boolean> {
    private static MDWorkflowManager INSTANCE;
    // 流程定义
    private static final Cc<String, WFlow> CC_FLOW = Cc.open();
    private static final Cc<String, JsonObject> CC_TODO = Cc.open();

    private static ProcessEngine ENGINE;
    private static HistoryEventHandler HANDLER;

    private MDWorkflowManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDWorkflowManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDWorkflowManager();
        }
        return INSTANCE;
    }

    public HistoryEventHandler camundaLogger() {
        return HANDLER;
    }

    public ProcessEngine camunda() {
        return ENGINE;
    }

    public Set<String> camundaBuiltIn() {
        final MetaWorkflow configRef = this.setting();
        return Optional.ofNullable(configRef)
            .map(MetaWorkflow::camundaBuiltIn)
            .orElseGet(HashSet::new);
    }

    public WFlow camundaFlow(final String flowCode) {
        return CC_FLOW.get(flowCode);
    }

    public JsonObject camundaTodo(final String todoKey) {
        return CC_TODO.get(todoKey);
    }

    Future<Boolean> compile(final MetaWorkflow config, final Vertx vertxRef) {
        // 引擎编译
        return this.compileEngine(config)
            // 流程定义抓取
            .compose(nil -> this.compileFlow(vertxRef))
            // 遗留 TOD_ 定义抓取
            .compose(nil -> this.compileLegacy());
    }

    private Future<Boolean> compileLegacy() {
        if (!CC_TODO.isEmpty()) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final String folder = "plugins/runtime/todo/";
        final List<String> files = Ut.ioFiles(folder, VValue.SUFFIX.JSON);
        if (files.isEmpty()) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        log.info("{} Todo 遗留定义：{}, 目录：{}", KeConstant.K_PREFIX_BOOT, files.size(), folder);
        files.forEach(file -> {
            final String path = folder + file;
            final JsonObject todoDef = Ut.ioJObject(path);
            final String key = file.replace(VString.DOT + VValue.SUFFIX.JSON, VString.EMPTY);
            CC_TODO.put(key, todoDef);
        });
        return Future.succeededFuture(Boolean.TRUE);
    }

    private Future<Boolean> compileEngine(final MetaWorkflow config) {
        if (Objects.nonNull(ENGINE)) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        final Database database = config.camundaDatabase();
        final ProcessEngineConfigurationImpl configuration = new StandaloneProcessEngineConfiguration()
            // Fix Issue:
            // org.camunda.bpm.engine.ProcessEngineException: historyLevel mismatch: configuration says HistoryLevelAudit(name=audit, id=2) and database says HistoryLevelFull(name=full, id=3)
            .setHistory(HistoryLevel.HISTORY_LEVEL_FULL.getName())     // none, audit, full, activity
            .setHistoryEventHandler(new DbHistoryEventHandler())
            // Fix Issue:
            // ENGINE-12019 The transaction isolation level set for the database is 'REPEATABLE_READ' which differs
            // from the recommended findRunning. Please change the isolation level to 'READ_COMMITTED' or set property
            // 'skipIsolationLevelCheck' to true. Please keep in mind that some levels are known to cause deadlocks
            // and other unexpected behaviours.
            .setSkipIsolationLevelCheck(true)
            .setIdGenerator(new StrongUuidGenerator())                 // uuid for task
            .setProcessEngineName(config.getName())
            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
            .setJdbcUrl(database.getUrl())
            .setJdbcDriver(database.getDriverClassName())
            .setJdbcUsername(database.getUsername())
            .setJdbcPassword(database.getPasswordDecrypted())
            .setJobExecutorActivate(true);
        HANDLER = configuration.getHistoryEventHandler();
        ENGINE = configuration.buildProcessEngine();
        log.info("{} Camunda 流程引擎初始化完成！！", KeConstant.K_PREFIX_BOOT);
        return Future.succeededFuture(Boolean.TRUE);
    }

    private Future<Boolean> compileFlow(final Vertx vertxRef) {
        final Configuration dbConfig = Ke.getConfiguration();
        final WFlowDao flowDao = new WFlowDao(dbConfig, vertxRef);
        return flowDao.findAll().compose(flows -> {
            log.info("{} 流程定义: {}", KeConstant.K_PREFIX_BOOT, flows.size());
            CC_FLOW.putAll(Ut.elementZip(flows, WFlow::getCode, flow -> flow));
            return Future.succeededFuture(Boolean.TRUE);
        });
    }
}
