package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.JobClient;
import io.zerows.cosmic.plugins.job.JobClientActor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UxJob {
    private transient final JobClient client;

    UxJob() {
        this.client = JobClientActor.ofClient();
    }

    // Start job
    public Future<Boolean> startAsync(final String code) {
        return this.client.startAsync(code).compose(result -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已启动, Timer = {}", code, result);
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    // Stop job
    public Future<Boolean> stopAsync(final String code) {
        return this.client.stopAsync(code).compose(result -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已停止。", code);
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    // Resume job
    public Future<Boolean> resumeAsync(final String code) {
        return this.client.resumeAsync(code).compose(result -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已恢复。", code);
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    public Future<JsonObject> statusAsync(final String namespace) {
        return this.client.statusAsync(namespace);
    }
}
