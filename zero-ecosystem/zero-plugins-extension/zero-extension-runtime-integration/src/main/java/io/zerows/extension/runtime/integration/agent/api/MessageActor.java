package io.zerows.extension.runtime.integration.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.metadata.commune.XHeader;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.integration.agent.service.MessageStub;
import io.zerows.extension.runtime.integration.eon.Addr;
import io.zerows.extension.runtime.skeleton.eon.em.EmMessage;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-04-02
 */
@Queue
public class MessageActor {

    @Inject
    private MessageStub messageStub;

    @Address(Addr.Message.FETCH_TYPED)
    public Future<JsonArray> fetchTyped(final String typeStr,
                                        final XHeader header,
                                        final User user) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("sendTo", Ux.keyUser(user));
        condition.put(KName.APP_ID, header.getAppId());

        final EmMessage.Type type = Ut.toEnum(typeStr, EmMessage.Type.class);
        return this.messageStub.fetchTyped(type, condition)
            .compose(Ux::futureA);
    }


    @Address(Addr.Message.UPDATE_STATUS)
    public Future<JsonArray> updateStatus(final String statusStr,
                                          final JsonArray keys,
                                          final User user) {
        final EmMessage.Status status = Ut.toEnum(statusStr, EmMessage.Status.class);
        return this.messageStub.updateStatus(keys, status, Ux.keyUser(user))
            .compose(Ux::futureA);
    }

    @Address(Addr.Message.ADD)
    @Me
    public Future<JsonObject> addMessage(final JsonObject data,
                                         final User user) {
        final String messageId = Ut.valueString(data, "messageId");
        data.put(KName.NAME, messageId);
        data.put(KName.CODE, messageId);
        return this.messageStub.addMessage(data, Ux.keyUser(user))
            .compose(Ux::futureJ);
    }

    @Address(Addr.Message.DELETE_BATCH)
    public Future<Boolean> deleteMessage(final JsonArray keys) {
        return this.messageStub.deleteMessage(keys);
    }
}
