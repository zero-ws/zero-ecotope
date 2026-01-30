package io.zerows.extension.module.workflow.component.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.spi.HPI;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RespectFile extends AbstractRespect {

    public RespectFile(final JsonObject query) {
        super(query);
    }

    @Override
    public Future<JsonArray> syncAsync(final JsonArray data, final JsonObject params, final WRecord record) {
        final JsonArray dataArray = this.syncPre(data, params, record);
        /*
         * Build condition based join
         * DEFAULT
         * - modelKey = key
         *
         * CONFIGURATION
         * - modelId = identifier
         * - modelCategory = `${flowDefinitionKey}`
         */
        final WTicket ticket = record.ticket();
        final JsonObject condition = this.queryTpl(ticket);
        condition.put(KName.MODEL_KEY, ticket.getId());
        return HPI.of(ExAttachment.class).waitAsync(
            file -> file.saveAsync(condition, dataArray, params),
            JsonArray::new
        );
    }

    @Override
    public Future<JsonArray> fetchAsync(final WRecord record) {
        final WTicket ticket = record.ticket();
        final JsonObject condition = this.queryTpl(ticket);
        condition.put(KName.MODEL_KEY, ticket.getId());
        return HPI.of(ExAttachment.class).waitAsync(
            link -> link.fetchAsync(condition),
            JsonArray::new
        );
    }

    /*
     *  Model Key
     */
    @Override
    protected void syncPre(final JsonObject data, final JsonObject params, final WRecord record) {
        final WTicket ticket = record.ticket();
        data.put(KName.MODEL_KEY, ticket.getId());
    }
}
