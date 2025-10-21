package io.zerows.extension.runtime.workflow.agent.api;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.runtime.workflow.agent.service.FlowStub;
import io.zerows.extension.runtime.workflow.agent.service.TaskStub;
import io.zerows.extension.runtime.workflow.domain.tables.daos.WTicketDao;
import io.zerows.extension.runtime.workflow.eon.HighWay;
import io.zerows.extension.runtime.workflow.exception._80600Exception404ProcessMissing;
import io.zerows.extension.runtime.workflow.uca.camunda.Io;
import io.zerows.extension.runtime.workflow.uca.transition.Vm;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.Objects;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class QueueActor {
    @Inject
    private transient FlowStub flowStub;
    @Inject
    private transient TaskStub taskStub;

    /*
     * The basic qr contains three view:
     * 1. The `owner` is userId
     * 2. The `supervisor` is userId
     * 3. The `openBy` is userId
     *
     * The input qr should be following:
     * {
     *     "criteria": {
     *     }
     * }
     * The criteria fields = [
     *      "owner, W_TICKET, I'm the ticket owner.",
     *      "supervisor, W_TICKET, I'm the ticket supervisor.",
     *      "openBy, W_TICKET, I'm the openBy."
     * ]
     *
     * W_TICKET Join W_TODO by `traceId`,
     * renamed field:
     * - W_TODO status -> statusT
     * - W_TODO serial -> serialT
     *
     * OLD COMMENT:
     * /*
     * Get condition of query running
     * W_TICKET JOINED W_TODO
     * Here are situations
     * 1. Default:
     * -- toUser is my ( For Approval )
     * -- openBy is my ( For Draft )
     *
     * 2. Provide condition
     *   2.1. User
     * -- owner
     * -- supervisor
     * -- openBy
     * -- toUser
     *   2.2. Assignment
     * -- toDept
     * -- toTeam
     * -- toRole
     * -- toGroup
     *   2.3. Range Search
     * -- owner -> Me
     * -- supervisor -> Me
     * -- openBy -> Me
     * -- toUser -> Me
     * -- toDept -> Employee -> Me ( Nid )
     * -- toTeam -> Employee -> Me ( Nid )
     * -- toRole -> Role -> Me ( Nid )
     * -- toGroup -> Group -> Me ( Nid )
     *   2.4. Basic Search
     * -- name + Me
     * -- code + Me
     * All the condition could visit to Me, it means that it's not needed to add
     * assignment person to condition here, but the system should still add condition
     * of `status = PENDING | ACCEPTED | DRAFT` here
     *
     * 3. Basic Condition
     * -- WTicket, flowEnd = false ( Is Running )
     * -- WTodo, status
     *    -- When opened ( PENDING, ACCEPTED, DRAFT )
     *    -- When approved ( PENDING, ACCEPTED )
     *
     * The code logical is as following
     * 1) When condition provided, DEFAULT
     * 2) When condition contains get ( Not Empty ), User/Assignment
     * History Queue based join WTicket
     * - flowEnd = true
     * - WTicket is ok to display in the done queue
     */
    @Address(HighWay.Queue.TASK_QUEUE)
    public Future<JsonObject> fetchQueue(final JsonObject qr) {
        LOG.Queue.info(this.getClass(), "Qr Queue Input: {0}", qr.encode());
        // Status Must be in following
        // -- PENDING
        // -- DRAFT
        // -- ACCEPTED
        final JsonObject qrStatus = Ux.whereAnd();
        qrStatus.put(KName.STATUS + ",i", Vm.Status.QUEUE);
        final JsonObject qrCombine = Ut.irAndQH(qr, "$Q$", qrStatus);
        LOG.Queue.info(this.getClass(), "Qr Queue Combined: {0}", qrCombine.encode());
        return this.taskStub.fetchQueue(qrCombine); // this.condStub.qrQueue(qr, userId)
    }


    @Address(HighWay.Queue.TICKET_HISTORY)
    public Future<JsonObject> fetchHistory(final JsonObject qr) {
        return this.taskStub.fetchHistory(qr);
    }

    @Address(HighWay.Flow.BY_CODE)
    public Future<JsonObject> fetchFlow(final String code, final XHeader header) {
        final String sigma = header.getSigma();
        return this.flowStub.fetchFlow(code, sigma);
    }

    @Address(HighWay.Queue.TASK_FORM)
    public Future<JsonObject> fetchForm(final JsonObject data,
                                        final Boolean isPre, final XHeader header) {
        // 「Predicate Checking」ProcessDefinition must be existing here
        final String definitionId = data.getString(KName.Flow.DEFINITION_ID);
        final Io<Task> ioTask = Io.ioTask();
        final ProcessDefinition definition = ioTask.inProcess(definitionId);
        if (Objects.isNull(definition)) {
            return FnVertx.failOut(_80600Exception404ProcessMissing.class, definitionId);
        }


        final String sigma = header.getSigma();
        if (isPre) {
            // 「Start」
            return this.flowStub.fetchForm(definition, sigma);
        }


        final String instanceId = data.getString(KName.Flow.INSTANCE_ID);
        final ProcessInstance instance = ioTask.inInstance(instanceId);
        if (Objects.isNull(instance)) {
            // 「End」
            final HistoricProcessInstance instanceHistory = ioTask.inHistoric(instanceId);
            return this.flowStub.fetchForm(instanceHistory, sigma);
        }


        // 「Run」
        final String taskId = data.getString(KName.Flow.TASK_ID);
        return ioTask.run(taskId).compose(task -> {
            // Fix: NullPointer for Task & Process
            if (Objects.isNull(task)) {
                return Ux.futureJ();
            }
            // Task Form ( Running )
            return this.flowStub.fetchForm(instance, task, sigma);
        });
    }


    @Address(HighWay.Flow.BY_TODO)
    public Future<JsonObject> fetchTodo(final String key, final User user) {
        final String userId = Ux.keyUser(user);
        return this.taskStub.readPending(key, userId);
    }

    @Address(HighWay.Flow.BY_HISTORY)
    public Future<JsonObject> fetchHistory(final String key) {
        return this.taskStub.readFinished(key);
    }

    @Address(HighWay.Queue.TICKET_LINKAGE)
    public Future<JsonObject> searchTicket(final JsonObject query) {
        return DB.on(WTicketDao.class).searchJAsync(query);
    }
}
