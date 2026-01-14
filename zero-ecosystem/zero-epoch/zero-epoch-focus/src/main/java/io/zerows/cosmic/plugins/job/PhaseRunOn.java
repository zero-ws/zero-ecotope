package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.cortex.metadata.ParameterBuilder;
import io.zerows.cosmic.plugins.job.exception._60041Exception417JobMethod;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
class PhaseRunOn {
    private transient final Vertx vertx;
    private transient final KRef underway = new KRef();

    PhaseRunOn(final Vertx vertx) {
        this.vertx = vertx;
    }

    PhaseRunOn bind(final KRef underway) {
        if (Objects.nonNull(underway)) {
            this.underway.add(underway.get());
        }
        return this;
    }

    Future<Envelop> invoke(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOn();
        if (Objects.nonNull(method)) {
            PhaseHelper.logOnce(mission, () ->
                log.debug("[ ZERO ] ( Job {} ) 3. --> 注解 @On 的方法调用：{} 。", mission.getCode(), method.getName()));
            return this.execute(envelop, method, mission);
        } else {
            return Ut.future(envelop);
        }
    }

    Future<Envelop> callback(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOff();
        if (Objects.nonNull(method)) {
            PhaseHelper.logOnce(mission, () ->
                log.debug("[ ZERO ] ( Job {} ) 6. <-- 注解 @Off 的方法调用：{} 。", mission.getCode(), method.getName()));
            return this.execute(envelop, method, mission);
        } else {
            return Ut.future(envelop);
        }

    }

    private Future<Envelop> execute(final Envelop envelop, final Method method, final Mission mission) {
        if (envelop.valid()) {
            final Object proxy = mission.getProxy();
            try {
                final Object[] arguments = this.buildArgs(envelop, method, mission);
                return Ut.invokeAsync(proxy, method, arguments)
                    .compose(this::normalize);
            } catch (final Throwable ex) {
                log.error(ex.getMessage(), ex);
                return Future.failedFuture(ex);
            }
        } else {
            PhaseHelper.logOnce(mission, () ->
                log.error("[ ZERO ] ( Job {} ) 任务出错终止，出错组件：{}", mission.getCode(), envelop.error().getClass().getName()));
            return Ut.future(envelop);
        }
    }

    private <T> Future<Envelop> normalize(final T returnValue) {
        if (Objects.isNull(returnValue)) {
            // Return null
            return Ut.future(Envelop.okJson());
        }
        if (Envelop.class == returnValue.getClass()) {
            return Future.succeededFuture((Envelop) returnValue);
        } else {
            return Ut.future(Envelop.success(returnValue));
        }
    }

    private Object[] buildArgs(final Envelop envelop, final Method method, final Mission mission) {
        /*
         * Available arguments:
         * -- Envelop
         * -- Mission
         * -- JsonObject -> Mission ( config )
         * */
        final Class<?>[] parameters = method.getParameterTypes();
        final List<Object> argsList = new ArrayList<>();
        if (0 < parameters.length) {

            final ParameterBuilder<Envelop> builder = ParameterJob.of(mission);
            for (final Class<?> parameterType : parameters) {
                // Old: TypedArgument.analyzeJob
                argsList.add(builder.build(envelop, parameterType, this.underway));
            }
        } else {
            throw new _60041Exception417JobMethod(mission.getCode());
        }
        return argsList.toArray();
    }
}
