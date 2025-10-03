package io.zerows.extension.runtime.workflow.uca.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Attachment;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTicket;
import io.zerows.program.Ux;

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
         * Build condition based on
         * DEFAULT
         * - modelKey = key
         *
         * CONFIGURATION
         * - modelId = identifier
         * - modelCategory = `${flowDefinitionKey}`
         */
        final WTicket ticket = record.ticket();
        final JsonObject condition = this.queryTpl(ticket);
        condition.put(KName.MODEL_KEY, ticket.getKey());

        // final JsonArray keys = Ut.valueJArray(dataArray, KName.KEY);
        // condition.put("key,!i", keys);
        return Ux.channelA(Attachment.class, Ux::futureA, file ->
            file.saveAsync(condition, dataArray, params));
        // Attachment Removing / Create
        // file.removeAsync(condition).compose(deleted -> file.uploadAsync(dataArray, params))
    }

    @Override
    public Future<JsonArray> fetchAsync(final WRecord record) {
        final WTicket ticket = record.ticket();
        final JsonObject condition = this.queryTpl(ticket);
        condition.put(KName.MODEL_KEY, ticket.getKey());
        return Ux.channelA(Attachment.class, Ux::futureA, link -> link.fetchAsync(condition));
    }

    /*
     *  Model Key
     */
    @Override
    protected void syncPre(final JsonObject data, final JsonObject params, final WRecord record) {
        final WTicket ticket = record.ticket();
        data.put(KName.MODEL_KEY, ticket.getKey());
    }
}
