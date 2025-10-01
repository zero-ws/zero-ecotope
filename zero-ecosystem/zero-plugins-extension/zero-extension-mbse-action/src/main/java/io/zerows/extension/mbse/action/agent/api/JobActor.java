package io.zerows.extension.mbse.action.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.mbse.action.agent.service.JobStub;
import io.zerows.extension.mbse.action.eon.JtAddr;
import jakarta.inject.Inject;

@Queue
public class JobActor {

    @Inject
    private transient JobStub stub;

    @Address(JtAddr.Job.START)
    public Future<Boolean> start(final String code) {
        return Ux.Job.on().startAsync(code);
    }

    @Address(JtAddr.Job.STOP)
    public Future<Boolean> stop(final String code) {
        return Ux.Job.on().stopAsync(code);
    }

    @Address(JtAddr.Job.RESUME)
    public Future<Boolean> resume(final String code) {
        return Ux.Job.on().resumeAsync(code);
    }

    @Address(JtAddr.Job.STATUS)
    public Future<JsonObject> status(final String namespace) {
        return Ux.Job.on().statusAsync(namespace);
    }

    /*
     * Basic Job api here
     */
    @Address(JtAddr.Job.BY_SIGMA)
    public Future<JsonObject> fetch(final String sigma, final JsonObject body, final Boolean grouped) {
        return this.stub.searchJobs(sigma, body, grouped);
    }

    @Address(JtAddr.Job.GET_BY_KEY)
    public Future<JsonObject> fetchByKey(final String key) {
        return this.stub.fetchByKey(key);
    }

    @Address(JtAddr.Job.UPDATE_BY_KEY)
    public Future<JsonObject> updateByKey(final String key, final JsonObject data) {
        return this.stub.update(key, data);
    }
}
