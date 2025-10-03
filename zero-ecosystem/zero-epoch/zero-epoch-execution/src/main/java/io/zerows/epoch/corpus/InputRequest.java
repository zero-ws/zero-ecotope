package io.zerows.epoch.corpus;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.metadata.security.AegisItem;
import io.zerows.platform.enums.EmSecure;
import io.zerows.support.Ut;
import io.zerows.sdk.security.Lee;
import io.zerows.sdk.security.LeeBuiltIn;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
class InputRequest {

    static <T> T request(final Message<Envelop> message, final Class<T> clazz) {
        final Envelop body = message.body();
        return request(body, clazz);
    }

    static <T> T request(final Envelop envelop, final Class<T> clazz) {
        if (Objects.isNull(envelop)) {
            return null;
        }
        return envelop.data(clazz);
    }

    static <T> T request(final Message<Envelop> message, final Integer index, final Class<T> clazz) {
        final Envelop body = message.body();
        return request(body, index, clazz);
    }

    static <T> T request(final Envelop envelop, final Integer index, final Class<T> clazz
    ) {
        if (Objects.isNull(envelop)) {
            return null;
        }
        return envelop.data(index, clazz);
    }

    static String requestUser(final Message<Envelop> message, final String field
    ) {
        return requestUser(message.body(), field);
    }

    static String requestUser(final Envelop envelop, final String field) {
        if (Objects.isNull(envelop)) {
            return null;
        }
        return envelop.identifier(field);
    }

    static String requestToken(final String tokenString, final String field) {
        String result = null;
        if (Ut.isNotNil(tokenString)) {
            final Lee lee = Ut.service(LeeBuiltIn.class);
            final JsonObject token = lee.decode(tokenString, AegisItem.configMap(EmSecure.AuthWall.JWT));
            if (Objects.nonNull(token)) {
                result = token.getString(field);
            }
        }
        return result;
    }

    static Object requestSession(
        final Message<Envelop> message,
        final String field
    ) {
        return requestSession(message.body(), field);
    }

    static Object requestSession(
        final Envelop envelop,
        final String field
    ) {
        if (Objects.isNull(envelop)) {
            return null;
        }
        final Session session = envelop.session();
        return null == session ? null : session.get(field);
    }

    static JsonArray assignValue(
        final JsonArray source,
        final JsonArray target,
        final String field,
        final boolean override
    ) {
        Ut.itJArray(source, JsonObject.class, (item, index) -> {
            if (override) {
                item.put(field, target.getValue(index));
            } else {
                if (!item.containsKey(field)) {
                    item.put(field, target.getValue(index));
                }
            }
        });
        return source;
    }

    static void assignAuditor(final Object reference, final boolean isUpdate) {
        if (Objects.nonNull(reference) && reference instanceof Envelop) {
            final Envelop envelop = (Envelop) reference;
            final String user = requestUser(envelop, "user");
            if (isUpdate) {
                envelop.value("updateBy", user);
                envelop.value("udpateTime", Instant.now());
            } else {
                envelop.value("key", UUID.randomUUID().toString());
                envelop.value("createBy", user);
                envelop.value("createTime", Instant.now());
            }
        }
    }
}
