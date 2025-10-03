package io.zerows.extension.runtime.workflow.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.workflow.agent.service.ReportStub;
import io.zerows.extension.runtime.workflow.agent.service.TaskStub;
import io.zerows.extension.runtime.workflow.eon.HighWay;
import io.zerows.extension.runtime.workflow.uca.transition.Vm;
import jakarta.inject.Inject;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class ReportActor {
    @Inject
    private transient ReportStub reportStub;
    @Inject
    private transient TaskStub taskStub;

    /*
    DRAFT Generation Data Request:
    {
        "openBy": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
        "toUser": "a0b1c6bc-4162-47e2-8f16-c9f4dd162739",
        "record": {
            "size": 1114042,
            "name": "error.jpeg",
            "sizeUi": "1.06MB",
            "type": "image/jpeg",
            "file": [
                {
                    "uid": "rc-upload-1643355423248-2",
                    "name": "error.jpeg",
                    "key": "ceafc8ec-0137-46df-a60f-38ae475b0242",
                    "type": "image/jpeg",
                    "size": 1114042,
                    "sizeUi": "1.06MB",
                    "extension": "jpeg"
                }
            ],
            "category": "FILE.REQUEST",
            "extension": "jpeg",
            "key": "ceafc8ec-0137-46df-a60f-38ae475b0242"
        },
        "toUserName": "开发者",
        "status": "DRAFT",
        "owner": "f7fbfaf9-8319-4eb0-9ee7-1948b8b56a67",
        "title": "TEST",
        "catalog": "w.document.request",
        "type": "workflow.doc",
        "description": "<p>TEST</p>",
        "openAt": "2022-01-28T07:37:06.141Z",
        "ownerName": "虞浪",
        "language": "cn",
        "active": true,
        "sigma": "Qxw5HDkluJFnAPmcQCtu9uhGdXEiGNtP",
        "workflow": {
            "definitionKey": "process.file.management",
            "definitionId": "process.file.management:1:c80c1ad1-7fd9-11ec-b990-f60fb9ea15d8"
        },
        "draft": true
    }
     */
    @Me
    @Address(HighWay.Report.TICKET_LIST)
    public Future<JsonObject> list(final JsonObject qr) {
        LOG.Queue.info(this.getClass(), "Qr Report Input: {0}", qr.encode());
        // Status Must be in following
        // -- PENDING
        // -- DRAFT
        // -- ACCEPTED
        final JsonObject qrStatus = Ux.whereAnd();
        qrStatus.put(KName.STATUS + ",i", Vm.Status.QUEUE);
        final JsonObject qrCombine = Ut.irAndQH(qr, "$Q$", qrStatus);
        LOG.Queue.info(this.getClass(), "Qr Report Combined: {0}", qrCombine.encode());
        return this.taskStub.fetchQueue(qrCombine); // this.condStub.qrQueue(qr, userId)
    }

    @Address(HighWay.Report.TICKET_ACTIVITY)
    public Future<JsonArray> fetchActivity(final JsonObject data) {
        final String modelKey = data.getString(KName.MODEL_KEY);
        final JsonObject user = data.getJsonObject(KName.USER);
        return (user == null) ?
            this.reportStub.fetchActivity(modelKey) :
            this.reportStub.fetchActivityByUser(modelKey, user.getString(KName.KEY));
    }
}
