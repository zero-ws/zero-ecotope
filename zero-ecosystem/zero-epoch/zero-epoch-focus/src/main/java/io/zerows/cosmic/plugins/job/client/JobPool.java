package io.zerows.cosmic.plugins.job.client;

import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.EmService;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Job Pool in memory or storage
 * Static pool for sync here.
 */
@Slf4j
class JobPool {

    public static final String IS_RUNNING = "[ ZERO ] ( Job ) 任务 {} 已经运行 !!!";
    public static final String IS_STARTING = "[ ZERO ] ( Job ) 任务 {} 正在启动，请等待它就绪 --> READY";
    public static final String IS_ERROR = "[ ZERO ] ( Job ) 任务 {} 上次运行遇到了问题，请联系管理员检查状态。";
    public static final String IS_STOPPED = "[ ZERO ] ( Job ) 任务的 timeId {} 没有存在于 RUNNING 任务表中.";
    public static final String NOT_RUNNING = "[ ZERO ] ( Job ) 任务 {} 没有运行，它的状态是 {}";

    private static final ConcurrentMap<String, Mission> JOBS = new ConcurrentHashMap<>();
    /* RUNNING Reference */
    private static final ConcurrentMap<Long, String> RUNNING = new ConcurrentHashMap<>();

    static void remove(final String code) {
        JOBS.remove(code);
    }

    static void save(final Mission mission) {
        if (Objects.nonNull(mission)) {
            JOBS.put(mission.getCode(), mission);
        }
    }

    static void bind(final Long timeId, final String code) {
        RUNNING.put(timeId, code);
    }


    static List<Mission> get() {
        return JOBS.values().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    static Mission get(final String code) {
        return JOBS.get(code);
    }

    /*
     * Started job
     * --> RUNNING
     */
    static void start(final Long timeId, final String code) {
        uniform(code, mission -> {
            final EmService.JobStatus status = mission.getStatus();
            if (EmService.JobStatus.RUNNING == status) {
                /*
                 * If `RUNNING`
                 * Do not started here because it's running now
                 */
                log.info(IS_RUNNING, code);
            } else if (EmService.JobStatus.ERROR == status) {
                /*
                 * If `ERROR`
                 * Could not started here
                 */
                log.warn(IS_ERROR, code);
            } else if (EmService.JobStatus.STARTING == status) {
                /*
                 * If `STARTING`
                 * Could not started here
                 */
                log.warn(IS_STARTING, code);
            } else {
                if (EmService.JobStatus.STOPPED == status) {
                    /*
                     * STOPPED -> READY
                     */
                    JOBS.get(code).setStatus(EmService.JobStatus.READY);
                }
                RUNNING.put(timeId, code);

            }
        });
    }

    /* Stop job --> Package Range */
    static void stop(final Long timeId) {
        uniform(timeId, mission -> {
            final EmService.JobStatus status = mission.getStatus();
            if (EmService.JobStatus.RUNNING == status || EmService.JobStatus.READY == status) {
                /*
                 * If `RUNNING`
                 * stop will trigger from
                 * RUNNING -> STOPPED
                 */
                RUNNING.remove(timeId);
                mission.setStatus(EmService.JobStatus.STOPPED);
            } else {
                /*
                 * Other status is invalid
                 */
                log.info(NOT_RUNNING, mission.getCode(), status);
            }
        });
    }

    /* Package Range */
    static void resume(final Long timeId) {
        uniform(timeId, mission -> {
            final EmService.JobStatus status = mission.getStatus();
            if (EmService.JobStatus.ERROR == status) {
                /*
                 * If `ERROR`
                 * resume will be triggered
                 * ERROR -> READY
                 */
                RUNNING.put(timeId, mission.getCode());
                mission.setStatus(EmService.JobStatus.READY);
            }
        });
    }

    static JsonObject status(final String namespace) {
        /*
         * Revert
         */
        final ConcurrentMap<String, Long> runsRevert =
            new ConcurrentHashMap<>();
        RUNNING.forEach((timer, code) -> runsRevert.put(code, timer));
        final JsonObject response = new JsonObject();
        JOBS.forEach((code, mission) -> {
            /*
             * Processing
             */
            final JsonObject instance = new JsonObject();
            instance.put(KName.NAME, mission.getName());
            instance.put(KName.STATUS, mission.getStatus().name());
            /*
             * Timer
             */
            instance.put(KName.TIMER, runsRevert.get(mission.getCode()));
            response.put(mission.getCode(), instance);
        });
        return response;
    }

    static String code(final Long timeId) {
        return RUNNING.get(timeId);
    }

    /* Package Range */
    static Long timeId(final String code) {
        return RUNNING.keySet().stream()
            .filter(key -> code.equals(RUNNING.get(key)))
            .findFirst().orElse(null);
    }

    private static void uniform(final Long timeId, final Consumer<Mission> consumer) {
        final String code = RUNNING.get(timeId);
        if (Ut.isNil(code)) {
            log.info(IS_STOPPED, timeId);
        } else {
            uniform(code, consumer);
        }
    }

    private static void uniform(final String code, final Consumer<Mission> consumer) {
        final Mission mission = JOBS.get(code);
        if (Objects.nonNull(mission)) {
            consumer.accept(mission);
        }
    }
}
