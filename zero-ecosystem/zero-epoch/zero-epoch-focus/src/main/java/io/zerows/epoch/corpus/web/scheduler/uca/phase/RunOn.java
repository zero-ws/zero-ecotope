package io.zerows.epoch.corpus.web.scheduler.uca.phase;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.log.OLog;
import io.zerows.platform.metadata.KRef;
import io.zerows.epoch.component.parameter.ParameterBuilder;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.corpus.web.exception._60041Exception417JobMethod;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.eon.MessageOfJob;
import io.zerows.epoch.corpus.web.scheduler.uca.parameter.ParameterJob;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class RunOn {
    private static final OLog LOGGER = Ut.Log.uca(RunOn.class);
    private transient final Vertx vertx;
    private transient final KRef underway = new KRef();

    RunOn(final Vertx vertx) {
        this.vertx = vertx;
    }

    RunOn bind(final KRef underway) {
        if (Objects.nonNull(underway)) {
            this.underway.add(underway.get());
        }
        return this;
    }

    Future<Envelop> invoke(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOn();
        if (Objects.nonNull(method)) {
            Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.PHASE_3RD_JOB_RUN, mission.getCode(), method.getName()));
            return this.execute(envelop, method, mission);
        } else {
            return Ut.future(envelop);
        }
    }

    Future<Envelop> callback(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOff();
        if (Objects.nonNull(method)) {
            Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.PHASE_6TH_JOB_CALLBACK, mission.getCode(), method.getName()));
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
                    /* Normalizing data */
                    .compose(this::normalize);
            } catch (final Throwable ex) {
                ex.printStackTrace();
                return Future.failedFuture(ex);
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageOfJob.PHASE.ERROR_TERMINAL, mission.getCode(), envelop.error().getClass().getName()));
            return Ut.future(envelop);
        }
    }

    private <T> Future<Envelop> normalize(final T returnValue) {
        if (Objects.isNull(returnValue)) {
            // Return null
            return Ut.future(Envelop.okJson());
        } else {
            if (Envelop.class == returnValue.getClass()) {
                return Future.succeededFuture((Envelop) returnValue);
            } else {
                return Ut.future(Envelop.success(returnValue));
            }
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
