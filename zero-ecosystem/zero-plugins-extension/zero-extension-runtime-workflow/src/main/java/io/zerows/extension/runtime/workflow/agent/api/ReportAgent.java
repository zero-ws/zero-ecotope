package io.zerows.extension.runtime.workflow.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.runtime.workflow.eon.HighWay;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * KRef actor part to get data formatFail
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface ReportAgent {
    // Testing Passed ---------------------------
    @POST
    @Path("/up/report/list")
    @Address(HighWay.Report.TICKET_LIST)
    JsonObject list(@BodyParam JsonObject body);

    @POST
    @Path("/up/report/activity")
    @Address(HighWay.Report.TICKET_ACTIVITY)
    JsonArray fetchActivity(@BodyParam JsonObject body);
}
