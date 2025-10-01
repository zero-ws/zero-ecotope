package io.zerows.epoch.corpus;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.corpus.web.scheduler.plugins.JobClient;
import io.zerows.epoch.corpus.web.scheduler.plugins.JobInfix;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.program.fn.Fx;

public class UxJob {
    private static final OLog LOGGER = Ut.Log.job(UxJob.class);
    private transient final JobClient client;

    UxJob() {
        this.client = JobInfix.getClient();
    }

    // Start job
    public Future<Boolean> startAsync(final String code) {
        return Fx.pack(future -> this.client.startAsync(code, res -> {
            LOGGER.info(INFO.UxJob.JOB_START, code, res.result());
            future.complete(Boolean.TRUE);
        }));
    }

    // Stop job
    public Future<Boolean> stopAsync(final String code) {
        return Fx.pack(future -> this.client.stopAsync(code,
            res -> {
                LOGGER.info(INFO.UxJob.JOB_STOP, code);
                future.complete(Boolean.TRUE);
            }));
    }

    // Resume job
    public Future<Boolean> resumeAsync(final String code) {
        return Fx.pack(future -> this.client.resumeAsync(code,
            res -> {
                LOGGER.info(INFO.UxJob.JOB_RESUME, code);
                future.complete(Boolean.TRUE);
            }));
    }

    public Future<JsonObject> statusAsync(final String namespace) {
        return this.client.statusAsync(namespace);
    }
}
