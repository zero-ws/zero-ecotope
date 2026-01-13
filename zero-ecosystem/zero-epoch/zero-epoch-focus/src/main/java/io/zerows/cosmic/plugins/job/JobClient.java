package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.constant.VString;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;

import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
@AddOn.Name("DEFAULT_JOB_CLIENT")
public interface JobClient {
    /*
     * Create local session findRunning bind data
     */
    static JobClient createClient(final Vertx vertx, final HConfig config) {
        return new JobClientImpl(vertx, config);
    }

    static void bind(final Long timerId, final String code) {
        JobControl.bind(timerId, code);
    }

    static String code(final String name) {
        return KWeb.JOB.NS + VString.DASH + name;
    }

    // ========================== UxJob mount

    /* Start */
    Future<Long> startAsync(String code);

    /* Stop */
    Future<Boolean> stopAsync(String code);

    /* Resume */
    Future<Long> resumeAsync(String timerId);

    // ========================== UxJob crud

    /* Fetch */
    Future<Mission> fetchAsync(String code);

    /* Fetch List */
    Future<List<Mission>> fetchAsync(Set<String> codes);

    Future<List<Mission>> fetchAsync();

    Mission fetch(String code);

    List<Mission> fetch(Set<String> codes);

    List<Mission> fetch();

    Future<Set<Mission>> saveAsync(Set<Mission> missions);

    /* Save */
    Future<Mission> saveAsync(Mission mission);

    Set<Mission> save(Set<Mission> missions);

    Mission save(Mission mission);

    /* Delete */
    Future<Mission> removeAsync(String code);

    Mission remove(String code);

    JsonObject status(String namespace);

    Future<JsonObject> statusAsync(String namespace);
}
