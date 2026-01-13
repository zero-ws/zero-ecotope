package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Defer;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Defer
class JobClientImpl implements JobClient {

    private transient final Vertx vertx;
    private transient final HConfig config;

    JobClientImpl(final Vertx vertx, final HConfig config) {
        this.vertx = vertx;
        this.config = config;
    }


    @Override
    public Future<Long> startAsync(final String code) {
        /* Find Mission by code */
        final Mission mission = JobControl.get(code);
        if (Objects.nonNull(mission)) {
            /* Start new job here */
            final Agha agha = Agha.get(mission.getType());
            /* Bind vertx */
            Ut.contract(agha, Vertx.class, this.vertx);
            /*
             * begin method return Future<Long>, it's async result
             * that's why here it's not needed to use:
             * Future.successedFuture() to wrapper result, instead
             * returned directly.
             * */
            return agha.begin(mission).compose(timerId -> {
                JobControl.start(timerId, code);
                return Future.succeededFuture(timerId);
            });
        } else {
            log.warn("[ ZERO ] ( JobClient ) 无法从任务池中找到 code = `{}` 的任务！", code);
            return Future.succeededFuture(-1L);
        }
    }

    @Override
    public Future<Boolean> stopAsync(final String code) {
        final Long timerId = JobControl.timeId(code);
        if (Objects.isNull(timerId)) {
            return Future.succeededFuture(Boolean.FALSE);
        }
        JobControl.stop(timerId);
        /* Cancel job */
        this.vertx.cancelTimer(timerId);
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Override
    public Future<Long> resumeAsync(final String code) {
        final Long timeId = JobControl.timeId(code);
        JobControl.resume(timeId);
        return this.startAsync(code);
    }

    @Override
    public Future<Mission> fetchAsync(final String code) {
        return Future.succeededFuture(this.fetch(code));
    }

    @Override
    public Mission fetch(final String code) {
        return JobControl.get(code);
    }

    @Override
    public List<Mission> fetch(final Set<String> codes) {
        final List<Mission> missionList = JobControl.get();
        if (Objects.isNull(codes) || codes.isEmpty()) {
            return new ArrayList<>();
        } else {
            return missionList.stream()
                .filter(mission -> codes.contains(mission.getCode()))
                .collect(Collectors.toList());
        }
    }

    @Override
    public Future<List<Mission>> fetchAsync(final Set<String> codes) {
        return Future.succeededFuture(this.fetch(codes));
    }

    @Override
    public Future<List<Mission>> fetchAsync() {
        return Future.succeededFuture(this.fetch());
    }

    @Override
    public List<Mission> fetch() {
        return JobControl.get();
    }

    @Override
    public Mission save(final Mission mission) {
        JobControl.save(mission);
        return mission;
    }

    @Override
    public Future<Mission> saveAsync(final Mission mission) {
        return Ut.future(this.save(mission));
    }


    @Override
    public Mission remove(final String code) {
        final Mission mission = this.fetch(code);
        JobControl.remove(code);
        return mission;
    }

    @Override
    public Future<Mission> removeAsync(final String code) {
        return Ut.future(this.remove(code));
    }

    @Override
    public Future<Set<Mission>> saveAsync(final Set<Mission> missions) {
        return Ut.future(this.save(missions));
    }

    @Override
    public Set<Mission> save(final Set<Mission> missions) {
        missions.forEach(this::save);
        return missions;
    }

    @Override
    public JsonObject status(final String namespace) {
        return JobControl.status(namespace);
    }

    @Override
    public Future<JsonObject> statusAsync(final String namespace) {
        return Ut.future(this.status(namespace));
    }
}
