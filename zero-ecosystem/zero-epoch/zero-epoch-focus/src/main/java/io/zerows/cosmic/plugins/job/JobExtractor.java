package io.zerows.cosmic.plugins.job;

import io.r2mo.base.util.R2MO;
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

import java.time.Duration;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class JobExtractor implements Extractor<Mission> {


    public static final String JOB_IGNORE = "[ ZERO ] ( Job ) 类 {} 使用了 @Job 注解，但没有定义 @On 的方法，将会被忽略。";

    @Override
    public Mission extract(final Class<?> clazz) {
        /*
         * 任务初始化
         */
        final Job annotation = clazz.getAnnotation(Job.class);
        if (Objects.isNull(annotation)) {
            /*
             * 如果未找到 @Job 注解，则返回 null
             */
            return null;
        }
        /* 默认类型 */
        final EmService.JobType type = annotation.value();

        /* 默认名称 -> 类名 */
        String name = annotation.name();
        name = Ut.isNil(name) ? clazz.getName() : name;

        /* 初始化配置 */
        final Mission mission = this.config(annotation);
        /*
         * 基础数据对象已初始化
         * 对于此类情况，任务名应等于别名
         * */
        mission.setName(name);
        mission.setReadOnly(Boolean.TRUE);

        /*
         * 允许通过配置设置类型，优先级说明：
         * 1) 注解中的类型优先级较低
         * 2) 配置中的类型优先级高于注解
         */
        if (Objects.isNull(mission.getType())) {
            mission.setType(type);
        }

        /* 每个任务的初始状态 */
        mission.setStatus(EmService.JobStatus.STARTING);

        {
            /* 阈值 / 阈值单位 */
            final Duration thresholdAt = R2MO.toDuration(annotation.threshold());
            mission.timeout(thresholdAt);
        }
        /* 设置定时器 */
        this.setTimer(mission, annotation);

        /* code 同步 */
        if (Ut.isNil(mission.getCode())) {
            mission.setCode(KWeb.JOB.NS + VString.DASH + mission.getName());
        }
        mission.connect(clazz);
        /* 必须存在 @On 方法 */
        if (Objects.isNull(mission.getOn())) {
            log.warn(JOB_IGNORE, clazz.getName());
            return null;
        }
        return mission;
    }

    private void setTimer(final Mission mission, final Job annotation) {
        /* 构建 Mission 的定时器 */
        final KScheduler timer = new KScheduler(mission.getCode());
        {
            /* 间隔 / 间隔单位 */
            final Duration durationAt = R2MO.toDuration(annotation.duration());
            // duration = durationUnit.toMillis(duration);
            timer.configure(durationAt);
        }
        /* 解析公式 */
        final String runFormula = annotation.formula();
        // Error-60054 Detect
        mission.detectPre(runFormula);
        timer.configure(runFormula, null);
        mission.timer(timer);
    }

    private Mission config(final Job annotation) {
        /* 读取配置 */
        final String config = annotation.config();
        final Mission mission;
        if (Ut.isNotNil(config)) {
            final JsonObject json = Ut.ioJObject(this.resolve(config));
            /*
             * 移除以下字段：
             * - status
             * - name
             * - type
             * 注意：此处 json 可能包含以下字段：
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
            /* config 包含 `/` 前缀 */
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
