package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.cosmic.plugins.job.JobClient;
import io.zerows.cosmic.plugins.job.JobClientAddOn;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

public class UxJob {
    private static final LogO LOGGER = Ut.Log.job(UxJob.class);
    private transient final JobClient client;

    UxJob() {
        this.client = JobClientAddOn.of().createSingleton();
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
