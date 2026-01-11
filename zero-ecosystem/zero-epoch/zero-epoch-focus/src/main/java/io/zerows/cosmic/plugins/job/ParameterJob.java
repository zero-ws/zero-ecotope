package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Session;
import io.zerows.cortex.metadata.ParameterBuilder;
import io.zerows.cosmic.plugins.job.exception._60041Exception417JobMethod;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.mbse.sdk.Commercial;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.extension.BodyParam;

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

    /**
     * 支持类型表
     * <pre>
     *     - {@link Envelop}
     *     - {@link Session}
     *     - {@link User}
     *     - {@link MultiMap}
     *     - {@link Commercial}
     *     - {@link JsonObject}
     *     - {@link Mission}
     *     - {@link KRef}
     * </pre>
     *
     * @param envelop  信封对象
     * @param type     参数类型
     * @param underway 特殊引用参数（任务专用）
     * @return 参数对象
     */
    @Override
    public Object build(final Envelop envelop, final Class<?> type, final KRef underway) {
        if (Envelop.class == type) {
            return envelop;
        } else if (is(type, Session.class)) {
            return envelop.session();
        } else if (is(type, User.class)) {
            return envelop.user();
        } else if (is(type, MultiMap.class)) {
            return envelop.headers();
        } else if (is(type, Commercial.class)) {
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
            if (type.isAnnotationPresent(BodyParam.class) ||
                type.isAnnotationPresent(BeanParam.class)) {
                return envelop.data();
            } else {
                return this.mission.getAdditional().copy();
            }
        } else if (is(type, Mission.class)) {
            return this.mission;
        } else if (is(type, KRef.class)) {
            return underway;
        } else {
            throw new _60041Exception417JobMethod(this.mission.getCode());
        }
    }
}
