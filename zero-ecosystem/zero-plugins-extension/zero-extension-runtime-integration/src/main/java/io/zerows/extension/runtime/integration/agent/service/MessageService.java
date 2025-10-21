package io.zerows.extension.runtime.integration.agent.service;

import io.r2mo.base.dbe.syntax.QSorter;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.runtime.integration.domain.tables.daos.IMessageDao;
import io.zerows.extension.runtime.integration.domain.tables.pojos.IMessage;
import io.zerows.extension.skeleton.common.enums.EmMessage;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-04-02
 */
public class MessageService implements MessageStub {

    @Override
    public Future<List<IMessage>> fetchTyped(final EmMessage.Type type, final JsonObject condition) {
        condition.put(KName.TYPE, type.name());
        return DB.on(IMessageDao.class).fetchAsync(condition,
            QSorter.of(KName.CREATED_AT, Boolean.FALSE));
    }

    @Override
    public Future<List<IMessage>> updateStatus(final JsonArray keys, final EmMessage.Status status, final String user) {
        final ADB jq = DB.on(IMessageDao.class);
        return jq.<IMessage>fetchInAsync(KName.KEY, keys).compose(messageList -> {
            messageList.forEach(message -> {
                message.setStatus(status.name());
                message.setUpdatedBy(user);
                message.setUpdatedAt(LocalDateTime.now());
            });
            return jq.updateAsync(messageList);
        });
    }

    @Override
    public Future<IMessage> addMessage(final JsonObject body, final String user) {
        // Query
        final JsonObject condition = Ux.whereAnd();
        {
            condition.put("sendTo", Ut.valueString(body, "sendTo"));
            condition.put(KName.SUBJECT, Ut.valueString(body, KName.SUBJECT));
            condition.put(KName.APP_ID, Ut.valueString(body, KName.APP_ID));
        }
        final ADB jq = DB.on(IMessageDao.class);
        return jq.<IMessage>fetchOneAsync(condition).compose(message -> {
            if (Objects.nonNull(message)) {
                return Ux.future(message);
            }

            final IMessage inserted = Ux.fromJson(body, IMessage.class);
            {
                final LocalDateTime at = LocalDateTime.now();
                inserted.setCreatedAt(at);
                inserted.setCreatedBy(user);
                inserted.setUpdatedAt(at);
                inserted.setUpdatedBy(user);
            }
            return jq.insertAsync(inserted);
        });
    }

    @Override
    public Future<Boolean> deleteMessage(final JsonArray keys) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.KEY + ",i", keys);
        return DB.on(IMessageDao.class).deleteByAsync(condition);
    }
}
