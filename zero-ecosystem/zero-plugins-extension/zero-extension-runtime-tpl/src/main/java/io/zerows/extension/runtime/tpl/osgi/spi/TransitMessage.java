package io.zerows.extension.runtime.tpl.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.skeleton.eon.em.EmMessage;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Transit;
import io.zerows.extension.runtime.tpl.domain.tables.daos.TplMessageDao;
import io.zerows.extension.runtime.tpl.domain.tables.pojos.TplMessage;
import io.zerows.extension.runtime.tpl.util.Tl;

import java.util.Objects;

/**
 * Message Tpl Processing
 *
 * @author lang : 2024-04-04
 */
public class TransitMessage implements Transit {
    @Override
    public Future<JsonObject> message(final JsonObject input) {
        // message 属性丢失，直接跳过
        final String message = Ut.valueString(input, KName.MESSAGE);
        if (Ut.isNil(message)) {
            return Ux.futureJ();
        }
        // TPL_MESSAGE 数据提取
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.APP_ID, Ut.valueString(input, KName.APP_ID));
        condition.put(KName.CODE, message);
        return Ux.Jooq.on(TplMessageDao.class).<TplMessage>fetchOneAsync(condition).compose(tpl -> {
            if (Objects.isNull(tpl)) {
                return Ux.futureJ();
            }

            final JsonObject params = Ut.valueJObject(input, KName.DATA, true);
            final JsonObject messageData = this.normalize(tpl, params);
            Tl.LOG.Tpl.info(this.getClass(), "Message Data: {0}", messageData.encode());
            return Ux.future(messageData);
        });
    }

    private JsonObject normalize(final TplMessage messageTpl, final JsonObject params) {
        final JsonObject message = new JsonObject();
        message.put(KName.TYPE, messageTpl.getType());

        final String exprSubject = messageTpl.getExprSubject();
        Objects.requireNonNull(exprSubject);
        final String formatted = Ut.fromExpression(exprSubject, params);
        message.put(KName.SUBJECT, formatted);

        final String exprContent = messageTpl.getExprContent();
        Objects.requireNonNull(exprContent);
        final String content = Ut.fromExpression(exprContent, params);
        message.put(KName.CONTENT, content);

        message.put(KName.STATUS, EmMessage.Status.SENT);
        return message;
    }
}
