package io.zerows.core.web.scheduler.eon;

/**
 * @author lang : 2024-04-22
 */
public
interface MessageOfJob {
    interface PHASE {

        String UCA_COMPONENT = "[ Job ] {0} selected: {1}";
        // ------------ io.vertx.up.operation.job.phase.OutPut / Input
        String UCA_EVENT_BUS = "[ Job ] {0} event bus enabled: {1}";
        // ------------ io.vertx.up.operation.job.phase.*
        String PHASE_1ST_JOB = "[ Job: {0} ] 1. Input new data of JsonObject";
        String PHASE_1ST_JOB_ASYNC = "[ Job: {0} ] 1. Input from address {1}";
        String PHASE_2ND_JOB = "[ Job: {0} ] 2. Input without `JobIncome`";
        String PHASE_2ND_JOB_ASYNC = "[ Job: {0} ] 2. Input with `JobIncome` = {1}";
        String PHASE_3RD_JOB_RUN = "[ Job: {0} ] 3. --> @On Method call {1}";
        String PHASE_6TH_JOB_CALLBACK = "[ Job: {0} ] 6. --> @Off Method call {1}";
        String PHASE_4TH_JOB_ASYNC = "[ Job: {0} ] 4. Output with `JobOutcome` = {1}";
        String PHASE_4TH_JOB = "[ Job: {0} ] 4. Output without `JobOutcome`";
        String PHASE_5TH_JOB = "[ Job: {0} ] 5. Output directly, ignore next EventBus steps";
        String PHASE_5TH_JOB_ASYNC = "[ Job: {0} ] 5. Output send to address {1}";
        String ERROR_TERMINAL = "[ Job: {0} ] Terminal with error: {1}";
    }

    interface INTERVAL {
        // ------------ io.vertx.up.operation.job.timer.Interval
        String START = "[ Job ] (timer = null) The job will start right now.";
        String RESTART = "[ Job ] (timer = null) The job will restart right now.";
        String DELAY_START = "[ Job ] `{0}` will start after `{1}`.";
        String DELAY_RESTART = "[ Job ] `{0}` will restart after `{1}`.";
        String SCHEDULED = "[ Job ] (timer = {0}) `{1}` scheduled duration {2} ms in each.";
    }

    // ------------ io.vertx.up.argument.worker.Mission
    interface MISSION {
        String JOB_OFF = "[ Job ] Current job `{0}` has defined @Off method.";
    }

    // ------------ C: io.aeon.experiment.specification.sch.KTimer
    interface TIMER {
        String DELAY = "[ Job ] Job \"{0}\" will started after `{1}` ";
    }

    // ------------ io.vertx.up.operation.job.get.JobStore
    interface STORE {
        String SCANNED = "[ Job ] The system scanned {0} jobs with type {1}";
    }

    interface PIN {
        // ------------ io.vertx.up.operation.job.get.JobPin
        String PIN_CONFIG = "[ Job ] Job configuration read : {0}";
    }

    interface AGHA {
        // ------------ io.vertx.up.operation.job.center.Agha
        String MOVED = "[ Job ] [{0}]（ Moved: {2} -> {3} ）, Job = `{1}`";
        String TERMINAL = "[ Job ] [{0}] The job will be terminal, status -> ERROR, Job = `{1}`";
        String WORKER_START = "[ Job ] `{0}` worker executor will be created. The max executing time is {1} s";
        String WORKER_END = "[ Job ] `{0}` worker executor has been closed! ";
    }
}
