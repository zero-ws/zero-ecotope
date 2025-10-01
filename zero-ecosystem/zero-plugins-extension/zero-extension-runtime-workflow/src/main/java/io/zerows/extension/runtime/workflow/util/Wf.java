package io.zerows.extension.runtime.workflow.util;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.uca.log.Log;
import io.zerows.epoch.common.uca.log.LogModule;
import io.zerows.extension.runtime.workflow.eon.em.PassWay;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

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
