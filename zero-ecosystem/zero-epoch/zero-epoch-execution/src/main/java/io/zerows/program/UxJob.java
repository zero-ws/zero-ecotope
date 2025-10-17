package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.JobClient;
import io.zerows.cosmic.plugins.job.JobClientActor;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UxJob {
    private transient final JobClient client;

    UxJob() {
        this.client = JobClientActor.ofDefault();
    }

    // Start job
    public Future<Boolean> startAsync(final String code) {
        return Fx.pack(future -> this.client.startAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已启动，任务 ID ：`{}` 。", code, res.result());
            future.complete(Boolean.TRUE);
        }));
    }

    // Stop job
    public Future<Boolean> stopAsync(final String code) {
        return Fx.pack(future -> this.client.stopAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已停止。", code);
            future.complete(Boolean.TRUE);
        }));
    }

    // Resume job
    public Future<Boolean> resumeAsync(final String code) {
        return Fx.pack(future -> this.client.resumeAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已恢复。", code);
            future.complete(Boolean.TRUE);
        }));
    }

    public Future<JsonObject> statusAsync(final String namespace) {
        return this.client.statusAsync(namespace);
    }
}
