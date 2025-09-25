package io.zerows.core.web.scheduler.uca.parameter;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Session;
import io.zerows.common.program.KRef;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.core.web.invocation.uca.parameter.ParameterBuilder;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.core.web.model.zdk.Commercial;
import io.zerows.core.web.scheduler.atom.Mission;
import io.zerows.core.web.scheduler.exception._417JobMethodException;

import java.util.Objects;

/**
 * @author lang : 2024-04-21
 */
public class ParameterJob implements ParameterBuilder<Envelop> {
    private static final Cc<String, ParameterBuilder<Envelop>> CCT_PARAM = Cc.openThread();

    private final Mission mission;

    private ParameterJob(final Mission mission) {
        this.mission = mission;
    }

    private static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || Ut.isImplement(paramType, expected);
    }

    public static ParameterBuilder<Envelop> of(final Mission mission) {
        Objects.requireNonNull(mission);
        return CCT_PARAM.pick(() -> new ParameterJob(mission), String.valueOf(mission.hashCode()));
    }

    @Override
    public Object build(final Envelop envelop, final Class<?> type, final KRef underway) {
        if (Envelop.class == type) {
            /*
             * Envelop
             */
            return envelop;
        } else if (is(type, Session.class)) {
            /*
             * 「ONCE Only」Session
             */
            return envelop.session();
        } else if (is(type, User.class)) {
            /*
             * 「ONCE Only」User
             */
            return envelop.user();
        } else if (is(type, MultiMap.class)) {
            /*
             * 「ONCE Only」Headers
             */
            return envelop.headers();
        } else if (is(type, Commercial.class)) {
            /*
             * Commercial specification
             */
            final JsonObject metadata = this.mission.getMetadata();
            final String className = metadata.getString(KName.__.CLASS);
            if (Ut.isNotNil(className)) {
                final Commercial commercial = Ut.instance(className);
                commercial.fromJson(metadata);
                return commercial;
            } else {
                return null;
            }
        } else if (is(type, JsonObject.class)) {
            if (type.isAnnotationPresent(BodyParam.class)) {
                /*
                 * @BodyParam, it's for data passing
                 */
                return envelop.data();
            } else {
                /*
                 * Non @BodyParam, it's for configuration of current job here.
                 * Return to additional data of JsonObject
                 * This method will be used in future.
                 */
                return this.mission.getAdditional().copy();
            }
        } else if (is(type, Mission.class)) {
            /*
             * Actor/Director must
             */
            return this.mission;
        } else if (is(type, KRef.class)) {
            /*
             * Bind Assist call here
             */
            return underway;
        } else {
            throw new _417JobMethodException(this.getClass(), this.mission.getCode());
        }
    }
}
