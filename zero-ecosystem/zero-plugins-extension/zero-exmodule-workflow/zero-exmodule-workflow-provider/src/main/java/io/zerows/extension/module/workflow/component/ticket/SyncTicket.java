package io.zerows.extension.module.workflow.component.ticket;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.workflow.component.toolkit.URequest;
import io.zerows.extension.module.workflow.component.toolkit.UTL;
import io.zerows.extension.module.workflow.domain.tables.daos.WTicketDao;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.metadata.MetaInstance;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.program.Ux;

class SyncTicket extends AbstractSync {

    SyncTicket(final MetaInstance metadata) {
        super(metadata);
    }

    @Override
    public Future<WRecord> treatAsync(final JsonObject requestJ, final WRecord recordIn, final WTicket ticketIn) {
        return Ux.future(ticketIn)

            // Before
            .compose(ticket -> UTL.beforeUpdate(ticket, recordIn))

            // Sync
            .compose(recordRef -> {
                final ADB tJq = DB.on(WTicketDao.class);
                final JsonObject ticketJ = requestJ.copy();
                /*
                 * Key Point for attachment linkage here, the linkage must contain
                 * serial part in params instead of distinguish between ADD / EDIT
                 */
                if (!requestJ.containsKey(KName.SERIAL)) {
                    requestJ.put(KName.SERIAL, ticketIn.getSerial());
                }

                // Compress InJson
                URequest.reduceJ(ticketJ);
                final WTicket combine = Ux.updateT(ticketIn, ticketJ);
                return tJq.updateAsync(combine);
            })

            // After
            .compose(updated -> UTL.afterUpdate(updated, recordIn));
    }
}
