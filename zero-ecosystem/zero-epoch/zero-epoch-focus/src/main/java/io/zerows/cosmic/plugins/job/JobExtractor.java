package io.zerows.cosmic.plugins.job;

import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.metadata.KScheduler;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Job;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmService;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class JobExtractor implements Extractor<Mission> {


    public static final String JOB_IGNORE = "[ ZERO ] ( Job ) 类 {} 使用了 @Job 注解，但没有定义 @On 的方法，将会被忽略。";

    @Override
    public Mission extract(final Class<?> clazz) {
        /*
         * Mission initializing
         */
        final Annotation annotation = clazz.getAnnotation(Job.class);
        if (Objects.isNull(annotation)) {
            /*
             * If Job annotation could not get, return null;
             */
            return null;
        }
        /* Default type */
        final EmService.JobType type = Ut.invoke(annotation, KName.VALUE);

        /* Default name -> class name */
        String name = Ut.invoke(annotation, KName.NAME);
        name = Ut.isNil(name) ? clazz.getName() : name;

        /* Initialization */
        final Mission mission = this.config(annotation);
        /*
         * Basic data object initialized
         * For this kind of situation, the job name should be equal to alias
         * */
        mission.setName(name);
        mission.setReadOnly(Boolean.TRUE);

        /*
         * Let type could be configured,
         * 1) Annotation type priority should be low
         * 2) Config type priority is higher than annotation
         */
        if (Objects.isNull(mission.getType())) {
            mission.setType(type);
        }

        /* The first status of each Job */
        mission.setStatus(EmService.JobStatus.STARTING);

        {
            /* threshold / thresholdUnit */
            final TimeUnit thresholdUnit = Ut.invoke(annotation, "thresholdUnit");
            final Integer threshold = Ut.invoke(annotation, "threshold");
            // threshold = thresholdUnit.toNanos(threshold);
            mission.timeout(threshold, thresholdUnit);
        }
        /* Set Timer */
        this.setTimer(mission, annotation);

        /* code sync */
        if (Ut.isNil(mission.getCode())) {
            mission.setCode(KWeb.JOB.NS + VString.DASH + mission.getName());
        }
        mission.connect(clazz);
        /* on method must existing */
        if (Objects.isNull(mission.getOn())) {
            log.warn(JOB_IGNORE, clazz.getName());
            return null;
        }
        return mission;
    }

    private void setTimer(final Mission mission, final Annotation annotation) {
        /* Timer of Mission Building */
        final KScheduler timer = new KScheduler(mission.getCode());
        {
            /* duration / durationUnit */
            final TimeUnit durationUnit = Ut.invoke(annotation, "durationUnit");
            final long duration = Ut.invoke(annotation, "duration");
            // duration = durationUnit.toMillis(duration);
            timer.configure(duration, durationUnit);
        }
        /* formula calculate */
        final String runFormula = Ut.invoke(annotation, "formula");
        // Error-60054 Detect
        mission.detectPre(runFormula);
        timer.configure(runFormula, null);
        mission.timer(timer);
    }

    private Mission config(final Annotation annotation) {
        /* Config */
        final String config = Ut.invoke(annotation, KName.CONFIG);
        final Mission mission;
        if (Ut.isNotNil(config)) {
            final JsonObject json = Ut.ioJObject(this.resolve(config));
            /*
             * Removed
             * - status
             * - name
             * - type
             * Be carefule, here include
             * - income
             * - incomeAddress
             * - outcome
             * - outcomeAddress
             * */
            json.remove(KName.STATUS);
            json.remove(KName.NAME);
            json.remove(KName.TYPE);
            json.remove("instant");
            mission = Ut.deserialize(json, Mission.class);
        } else {
            mission = new Mission();
        }
        return mission;
    }

    private String resolve(final String config) {
        final StringBuilder file = new StringBuilder(KWeb.JOB.PREFIX);
        if (config.startsWith(VString.SLASH)) {
            /* config contains `/` prefix */
            file.append(config);
        } else {
            file.append(VString.SLASH).append(config);
        }
        if (!config.endsWith(VString.DOT + VValue.SUFFIX.JSON)) {
            file.append(VString.DOT).append(VValue.SUFFIX.JSON);
        }
        return file.toString().replace("//", "/");
    }
}
