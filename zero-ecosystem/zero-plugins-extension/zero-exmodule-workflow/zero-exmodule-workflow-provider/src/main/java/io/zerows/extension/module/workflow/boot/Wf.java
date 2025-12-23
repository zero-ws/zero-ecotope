package io.zerows.extension.module.workflow.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;
import io.zerows.extension.module.workflow.common.em.PassWay;
import io.zerows.extension.module.workflow.domain.tables.pojos.WFlow;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class Wf {

    /*
     * Workflow Output
     * {
     *      "definitionId": "Process Definition Id",
     *      "definitionKey": "Process Definition Key",
     *      "bpmn": "Xml formatFail of BPMN 2.0 diagram",
     *      "name": "Process Definition Name"
     * }
     */
    public static JsonObject outBpmn(final ProcessDefinition definition) {
        return WfFlow.outBpmn(definition);
    }

    public static JsonObject outLinkage(final JsonObject linkageJ) {
        return WfFlow.outLinkage(linkageJ);
    }

    public static PassWay inGateway(final JsonObject requestJ) {
        return WfFlow.inGateway(requestJ);
    }

    public static String nameEvent(final Task task) {
        return WfFlow.nameEvent(task);
    }

    public static List<Task> taskNext(final Task task, final List<Task> source) {
        return WfFlow.taskNext(task, source);
    }

    public static RepositoryService camundaRepository() {
        return MDWorkflowManager.of().camunda().getRepositoryService();
    }

    public static FormService camundaForm() {
        return MDWorkflowManager.of().camunda().getFormService();
    }

    public static RuntimeService camundaRuntime() {
        return MDWorkflowManager.of().camunda().getRuntimeService();
    }

    public static TaskService camundaTask() {
        return MDWorkflowManager.of().camunda().getTaskService();
    }

    public static HistoryService camundaHistory() {
        return MDWorkflowManager.of().camunda().getHistoryService();
    }

    public static HistoryEventHandler camundaLogger() {
        return MDWorkflowManager.of().camundaLogger();
    }

    public static JsonObject getTodo(final String type) {
        return MDWorkflowManager.of().camundaTodo(type);
    }

    public static WFlow getFlow(final String code) {
        return MDWorkflowManager.of().camundaFlow(code);
    }

    public static Set<String> getBuiltIn() {
        return MDWorkflowManager.of().camundaBuiltIn();
    }

    public interface LOG {
        String MODULE = "Ροή εργασίας";

        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Queue = Log.modulat(MODULE).extension("Queue");
        LogModule Deploy = Log.modulat(MODULE).extension("Deploy");
        LogModule Move = Log.modulat(MODULE).extension("Move");
        LogModule Plugin = Log.modulat(MODULE).extension("Infusion");
        LogModule Web = Log.modulat(MODULE).extension("Web");
    }
}
