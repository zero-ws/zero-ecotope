package io.zerows.extension.runtime.workflow.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.common.shared.boot.KPivot;
import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.configuration.module.MDWorkflow;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.epoch.mem.OZeroStore;
import io.zerows.epoch.support.FnBase;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WFlow;
import io.zerows.extension.runtime.workflow.uca.deployment.DeployOn;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.configuration.boot.HRegistry;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class WfPin implements HRegistry.Mod<Vertx> {

    public static RepositoryService camundaRepository() {
        return WfConfiguration.camunda().getRepositoryService();
    }

    public static FormService camundaForm() {
        return WfConfiguration.camunda().getFormService();
    }

    public static RuntimeService camundaRuntime() {
        return WfConfiguration.camunda().getRuntimeService();
    }

    public static TaskService camundaTask() {
        return WfConfiguration.camunda().getTaskService();
    }

    public static HistoryService camundaHistory() {
        return WfConfiguration.camunda().getHistoryService();
    }

    public static HistoryEventHandler camundaLogger() {
        return WfConfiguration.camundaLogger();
    }

    public static JsonObject getTodo(final String type) {
        return WfTodo.getTodo(type);
    }

    public static WFlow getFlow(final String code) {
        return WfConfiguration.workflow(code);
    }

    public static Set<String> getBuiltIn() {
        return WfConfiguration.camundaBuiltIn();
    }

    /**
     * 新版模块注册器，工作流模块的注册机制和其他模块不同，由于此处存在大量的新库访问以及 Camunda 流程引擎
     * 的调用，因此此处的注册器需要异步执行，而且需要等待 Camunda 引擎初始化完成之后才能执行。
     */
    @Override
    public Future<Boolean> configureAsync(final Vertx container, final HAmbient ambient) {
        // 1. 检查是否启用
        if (!OZeroStore.is(YmlCore.workflow.__KEY)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        // 2. 遗留系统待办模块初始化
        {
            LOG.Init.info(WfPin.class, "WfTodo...");
            WfTodo.initLegacy();
        }
        // 3. 正式初始化工作流
        Ke.banner("「Ροή εργασίας」- ( Workflow )");
        LOG.Init.info(WfPin.class, "WfConfiguration...");

        return WfConfiguration.registry(KPivot.running(), container).compose(configured -> {
            LOG.Init.info(WfPin.class, "Workflow Engine Start...");

            final Set<MDWorkflow> workflows = new HashSet<>();
            HExtension.keySet().forEach(bundleName -> {
                final MDConfiguration configuration = HExtension.getOrCreate(bundleName);
                workflows.addAll(configuration.inWorkflow());
            });
            LOG.Init.info(WfPin.class, "Here are {0} folder that will be waited for deployment...",
                String.valueOf(workflows.size()));
            final List<Future<Boolean>> futures = new ArrayList<>();
            // Deployment for .bpmn files
            workflows.forEach(workflow -> DeployOn.get(workflow).initialize());
            return FnBase.combineT(futures).compose(nil -> Future.succeededFuture(Boolean.TRUE));
        });
    }
}
