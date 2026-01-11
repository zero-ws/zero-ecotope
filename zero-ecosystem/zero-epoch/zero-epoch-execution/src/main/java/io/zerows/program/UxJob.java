package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.JobClient;
import io.zerows.cosmic.plugins.job.JobClientActor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class UxJob {
    private transient final JobClient client;

    UxJob() {
        this.client = JobClientActor.ofClient();
    }

    private static <T> Future<T> waitForConsumer(final Consumer<Promise<T>> consumer) {
        final Promise<T> promise = Promise.promise();
        consumer.accept(promise);
        return promise.future();
    }

    // Start job
    public Future<Boolean> startAsync(final String code) {
        return waitForConsumer(future -> this.client.startAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已启动，任务 ID ：`{}` 。", code, res.result());
            future.complete(Boolean.TRUE);
        }));
    }

    // Stop job
    public Future<Boolean> stopAsync(final String code) {
        return waitForConsumer(future -> this.client.stopAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已停止。", code);
            future.complete(Boolean.TRUE);
        }));
    }

    // Resume job
    public Future<Boolean> resumeAsync(final String code) {
        return waitForConsumer(future -> this.client.resumeAsync(code, res -> {
            log.info("[ ZERO ] ( UxJob ) 任务 code = `{}` 已恢复。", code);
            future.complete(Boolean.TRUE);
        }));
    }

    public Future<JsonObject> statusAsync(final String namespace) {
        return this.client.statusAsync(namespace);
    }
}
