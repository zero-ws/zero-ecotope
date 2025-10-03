package io.zerows.epoch.corpus.io.uca.response.hooker;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.SessionData;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author lang : 2024-04-04
 */
public class LaterSession extends AbstractLater<Object> {
    protected LaterSession(final RoutingContext context) {
        super(context);
    }

    @Override
    public void execute(final Object message, final Method hooker) {
        final Session session = this.session();
        if (Objects.isNull(session) || Objects.isNull(message)) {
            return;
        }
        if (Objects.isNull(hooker) || !hooker.isAnnotationPresent(SessionData.class)) {
            return;
        }

        final Annotation annotation = hooker.getAnnotation(SessionData.class);
        final String key = Ut.invoke(annotation, KName.VALUE);
        final String field = Ut.invoke(annotation, KName.FIELD);
        // Data Storage
        Object reference = message;
        if (Ut.isJObject(message) && Ut.isNotNil(field)) {
            final JsonObject target = (JsonObject) message;
            reference = target.getValue(field);
        }
        // Session Put / Include Session ID
        session.put(key, reference);
    }
}
