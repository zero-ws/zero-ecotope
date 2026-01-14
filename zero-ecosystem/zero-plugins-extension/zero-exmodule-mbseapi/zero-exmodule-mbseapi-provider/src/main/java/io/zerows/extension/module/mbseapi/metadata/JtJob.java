package io.zerows.extension.module.mbseapi.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.KScheduler;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.extension.module.mbseapi.boot.Jt;
import io.zerows.extension.module.mbseapi.common.JtKey;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IJob;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.platform.enums.EmService;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/*
 * Job ( JOB + SERVICE )
 */
public class JtJob extends JtCommercial {
    private transient IJob job;
    private transient String key;

    /*
     * For deserialization
     */
    public JtJob() {
    }

    public JtJob(final IJob job, final IService service) {
        super(service);
        this.job = job;
        /* */
        this.key = job.getKey();
    }
    // ----------- override

    @Override
    public JsonObject options() {
        return Jt.toOptions(this.service(), this.job);
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public JsonObject toJson() {
        final JsonObject data = super.toJson();
        /* key data */
        data.put(JtKey.Delivery.JOB, Ut.serializeJson(this.job));
        return data;
    }

    @Override
    public void fromJson(final JsonObject data) {
        super.fromJson(data);
        /*
         * Basic attributes
         */
        this.key = data.getString(JtKey.Delivery.KEY);
        /*
         * job
         */
        this.job = Ut.deserialize(data.getJsonObject(JtKey.Delivery.JOB), IJob.class);
    }

    // ----------- job & service

    public Mission toJob() {
        final Mission mission = new Mission();
        /*
         * IJob -> Mission：code
         * 1) Job alias is job name for standalone here
         * 2) Job code/name must be `namespace + code` to web unique identifier of current job
         * 3) Default job type is ONCE
         * 4) For job configuration, it's different for
         * - 4.1) All the programming job should be `READONLY` ( hard coding )
         * - 4.2) All the extension job ( stored into database ) should be `EDITABLE` ( dynamic )
         */
        mission.setName(this.job.getName());
        mission.setType(Ut.toEnum(this.job::getType, EmService.JobType.class, EmService.JobType.ONCE));
        mission.setCode(Jt.jobCode(this.job));
        mission.setReadOnly(Boolean.FALSE);
        /* Basic information */
        mission.setComment(this.job.getComment());
        mission.setAdditional(Ut.toJObject(this.job.getAdditional()));
        /* Set job configuration of current environment. bind to `service` */
        mission.setMetadata(this.toJson().copy());


        /*
         * EmApp `name` missing in future
         * JtApp processing
         */
        final IService service = this.service();
        final HArk ark = Ke.ark(service.getSigma());
        mission.ark(ark);
        final Duration timeoutAt = Duration.of(this.job.getThreshold(), ChronoUnit.MINUTES);
        mission.timeout(timeoutAt);

        this.setTimer(mission);
        /*
         * Component executor here.
         * KIncome / incomeAddress
         */
        if (Objects.nonNull(this.job.getIncomeComponent())) {
            mission.setIncome(Ut.clazz(this.job.getIncomeComponent()));
        }
        mission.setIncomeAddress(this.job.getIncomeAddress());
        /*
         * Outcome / outcomeAddress
         */
        if (Objects.nonNull(this.job.getOutcomeComponent())) {
            mission.setOutcome(Ut.clazz(this.job.getOutcomeComponent()));
        }
        mission.setOutcomeAddress(this.job.getOutcomeAddress());
        return this.mount(mission);
    }

    private void setTimer(final Mission mission) {
        /*
         * Scheduler of Mission definition here and you can set any information
         * of current Part.
         */
        final KScheduler timer = new KScheduler(mission.getCode());
        final String runFormula = this.job.getRunFormula();
        // Error-60054 Detect
        mission.detectPre(runFormula);
        final EmService.JobType type = mission.getType();
        if (EmService.JobType.ONCE != type) {
            timer.configure(runFormula, this.job.getRunAt());
            if (Objects.nonNull(this.job.getDuration())) {
                final Duration scheduledAt = Duration.of(this.job.getDuration(), ChronoUnit.MINUTES);
                timer.configure(scheduledAt);
            }
            mission.scheduler(timer);
        }
    }

    private Mission mount(final Mission mission) {
        final String proxyStr = this.job.getProxy();
        final Class<?> clazz = Ut.clazz(proxyStr);
        if (Objects.nonNull(clazz)) {
            /*
             * Object initialized
             */
            return mission.connect(clazz);
        } else {
            return mission;
        }
    }
}
