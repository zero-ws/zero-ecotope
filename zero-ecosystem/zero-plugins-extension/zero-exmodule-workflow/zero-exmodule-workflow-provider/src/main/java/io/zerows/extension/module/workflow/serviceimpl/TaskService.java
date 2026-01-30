package io.zerows.extension.module.workflow.serviceimpl;

import io.r2mo.base.dbe.Join;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.workflow.component.toolkit.ULinkage;
import io.zerows.extension.module.workflow.domain.tables.daos.WTicketDao;
import io.zerows.extension.module.workflow.domain.tables.daos.WTodoDao;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.extension.module.workflow.metadata.EngineOn;
import io.zerows.extension.module.workflow.metadata.MetaInstance;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.module.workflow.servicespec.TaskStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Objects;

import static io.zerows.extension.module.workflow.boot.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TaskService implements TaskStub {
    @Inject
    private transient AclStub aclStub;

    @Override
    public Future<JsonObject> fetchQueue(final JsonObject condition) {
        final JsonObject combine = Ut.irAndQH(condition, KName.Flow.FLOW_END, Boolean.FALSE);
        return DB.on(Join.of(
                WTodoDao.class, KName.Flow.TRACE_ID,
                WTicketDao.class
            ))
            .alias(WTicketDao.class, Map.of(
                KName.KEY, KName.Flow.TRACE_KEY,
                KName.SERIAL, KName.Flow.TRACE_SERIAL,
                KName.CODE, KName.Flow.TRACE_CODE
            ))
            .searchAsync(combine);
        //        return DB.join()
        //
        //            // Join WTodo Here
        //            .add(WTodoDao.class, KName.Flow.TRACE_ID)
        //            .join(WTicketDao.class)
        //
        //            // Alias must be called after `add/join`
        //            .alias(WTicketDao.class, new JsonObject()
        //                .put(KName.KEY, KName.Flow.TRACE_KEY)
        //                .put(KName.SERIAL, KName.Flow.TRACE_SERIAL)
        //                .put(KName.CODE, KName.Flow.TRACE_CODE)
        //            )
        //            .searchAsync(combine);
    }

    @Override
    public Future<JsonObject> fetchHistory(final JsonObject condition) {
        final JsonObject combine = Ut.irAndQH(condition, KName.Flow.FLOW_END, Boolean.TRUE);
        return DB.on(WTicketDao.class).searchJAsync(combine);
    }

    // ====================== Single Record
    @Override
    public Future<JsonObject> readPending(final String key, final String userId) {
        final WRecord record = WRecord.create();


        // Read Todo Record
        // Extract traceId from WTodo
        return this.readTodo(key, record).compose(processed -> {
            final WTodo todo = processed.task();
            if (Objects.isNull(todo)) {
                LOG.Web.info(this.getClass(), "Ticket Status Conflict, key = {0}", key);
                return Ux.futureJ();
            } else {
                return Ux.future(todo.getTraceId())

                    // Read Ticket Record
                    .compose(ticketId -> this.readTicket(ticketId, record))


                    // Linkage
                    .compose(ULinkage::readLinkage)


                    // Child
                    .compose(this::readChild)


                    // Generate JsonObject of response
                    .compose(wData -> wData.futureJ(false))


                    // Acl Mount
                    .compose(response -> this.aclStub.authorize(record, userId)
                        .compose(acl -> Ux.future(response.put(KName.__.ACL, acl)))
                    );
            }
        });
    }

    private Future<WRecord> readTodo(final String key, final WRecord response) {
        return DB.on(WTodoDao.class).<WTodo>fetchByIdAsync(key)
            .compose(item -> Future.succeededFuture(response.task(item)));
    }

    private Future<WRecord> readTicket(final String key, final WRecord response) {
        return DB.on(WTicketDao.class).<WTicket>fetchByIdAsync(key)
            .compose(ticket -> Future.succeededFuture(response.ticket(ticket)));
    }

    private Future<WRecord> readChild(final WRecord response) {
        final WTicket ticket = response.ticket();
        Objects.requireNonNull(ticket);

        // Connect to Workflow Engine
        final EngineOn engine = EngineOn.connect(ticket.getFlowDefinitionKey());
        final MetaInstance meta = engine.metadata();

        // Read Child
        final ADB jq = meta.childDao();
        if (Objects.isNull(jq)) {
            return Ux.future(response);
        }
        return jq.fetchJByIdAsync(ticket.getId())
            // ChildOut
            .compose(queried -> Ux.future(meta.childOut(queried)))
            .compose(queried -> Ux.future(response.ticket(queried)));
    }


    @Override
    public Future<JsonObject> readFinished(final String key) {
        final WRecord record = WRecord.create();


        // Read Ticket Record
        return this.readTicket(key, record)


            // Linkage
            .compose(ULinkage::readLinkage)


            // Child
            .compose(this::readChild)


            // Generate JsonObject of response
            .compose(wData -> wData.futureJ(true));
    }
}
