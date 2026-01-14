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
            return null;
        }

        /* 1. 初始化配置 ( Configuration Loading ) */
        final JsonObject config = this.configuration(annotation);

        /* 2. 初始化任务 ( Mission Initialization ) */
        final Mission mission = this.mission(config);

        /* 3. 加载基础属性 ( Name, Type, Status, Code ) */
        this.configureBasic(mission, annotation, clazz);

        /* 4. 设置阈值 ( Threshold ) */
        this.configureThreshold(mission, annotation, config);

        /* 5. 设置定时器 ( Timer ) */
        this.configureTimer(mission, annotation, config);

        mission.connect(clazz);
        /* 必须存在 @On 方法 */
        if (Objects.isNull(mission.getOn())) {
            log.warn(JOB_IGNORE, clazz.getName());
            return null;
        }
        return mission;
    }

    /**
     * <pre>
     * ⚙️ 配置基础属性 (Basic Configuration)
     *
     * 1. Name (名称):
     *    - 编程优先：注解 > 配置 > 类名
     * 2. ReadOnly (只读):
     *    - 固定为 true，表示该任务由代码定义。
     * 3. Type (类型):
     *    - 编程优先：配置 > 注解
     * 4. Status (状态):
     *    - 初始状态设定为 STARTING。
     * 5. Code (标识):
     *    - 若未指定，生成默认标识：JOB-MISSION-{name}
     * </pre>
     *
     * @param mission    任务对象
     * @param annotation Job 注解
     * @param clazz      目标类
     */
    private void configureBasic(final Mission mission, final Job annotation, final Class<?> clazz) {
        /*
         * 1. 名称处理 (Name)
         * 编程优先模式：
         * 1) 如果注解设置了 name，则优先使用注解配置
         * 2) 如果注解未设置，且配置文件中配置了 name，则使用配置文件的
         * 3) 如果都未设置，则使用类名作为默认名称
         */
        final String name = annotation.name();
        if (Ut.isNotNil(name)) {
            mission.setName(name);
        }
        if (Ut.isNil(mission.getName())) {
            mission.setName(clazz.getName());
        }

        mission.setReadOnly(Boolean.TRUE);

        /*
         * 允许通过配置设置类型，优先级说明：
         * 1) 注解中的类型优先级较低
         * 2) 配置中的类型优先级高于注解
         */
        if (Objects.isNull(mission.getType())) {
            mission.setType(annotation.value());
        }

        /* 每个任务的初始状态 */
        mission.setStatus(EmService.JobStatus.STARTING);

        /*
         * 🔄 Code 同步与生成 (Identity Generation)
         * 检查并生成任务的唯一标识 (Code)。若未配置，则使用标准前缀生成：
         * 格式：JOB-MISSION-{name}
         */
        if (Ut.isNil(mission.getCode())) {
            mission.setCode(KWeb.JOB.NS + VString.DASH + mission.getName());
        }
    }

    /**
     * <pre>
     * 🛡️ 配置阈值 (Threshold Configuration)
     *
     * 配置优先模式处理任务超时阈值：
     * 1. 优先读取配置文件中的 threshold 属性。
     * 2. 若配置未定义，则读取 @Job 注解中的 threshold 属性。
     * 3. 解析字符串为 Duration 对象并设置到 Mission 中。
     * </pre>
     *
     * @param mission    任务对象
     * @param annotation Job 注解
     * @param config     配置对象
     */
    private void configureThreshold(final Mission mission, final Job annotation, final JsonObject config) {
        /*
         * 4. 阈值处理 (Threshold)
         * 配置优先模式：
         * 1) 优先读取配置文件中的 threshold
         * 2) 如果配置文件未设置，使用注解的配置
         */
        String threshold = annotation.threshold();
        if (Ut.isNotNil(config) && Ut.isNotNil(config.getString("threshold"))) {
            threshold = config.getString("threshold");
        }

        if (Ut.isNotNil(threshold)) {
            final Duration thresholdAt = R2MO.toDuration(threshold);
            if (Objects.nonNull(thresholdAt)) {
                mission.timeout(thresholdAt);
            }
        }
    }

    /**
     * <pre>
     * ⏰ 配置定时器 (Timer Configuration)
     *
     * 核心逻辑：
     * 1. 根据 Mission Code 初始化 KScheduler。
     * 2. 解析时间间隔 (Duration): 配置优先 (配置 > 注解)。
     * 3. 解析运行公式 (Formula)。
     * 4. 将定时器绑定到任务。
     *
     * ⚠️ 注意事项:
     * 该方法必须在 configureBasic 之后调用，因为 KScheduler 依赖 Mission Code。
     * </pre>
     *
     * @param mission    任务对象
     * @param annotation Job 注解
     * @param config     配置对象
     */
    private void configureTimer(final Mission mission, final Job annotation, final JsonObject config) {
        /*
         * 构建 Mission 的定时器，mission 的 code 是在运行时才会确定，所以此处
         * 和 Mission 绑定的 KScheduler 中使用名称作为标识符。
         *
         * ⚠️ 顺序依赖说明 (Critical Order):
         * setTimer() 必须严格在 setCode() 之后执行。
         *
         * 原因详解:
         * 1. 标识依赖: KScheduler 的初始化 (new KScheduler(code)) 强依赖于 Mission 的 Code。
         * 2. 调度绑定: 只有拥有了唯一标识 Code，调度器才能正确地将定时策略绑定到该任务上。
         * 3. 避免游离: 若 Code 为空初始化 Timer，将导致产生“游离”的调度器，使得任务无法被 JobPool 正确管理（查找、停止、恢复）。
         **/
        final KScheduler timer = new KScheduler(mission.getCode());
        {
            /*
             * 3. 间隔处理 (Duration)
             * 配置优先模式：
             * 1) 优先读取配置文件中的 configuration
             * 2) 如果配置文件未设置，读取注解中的 duration
             */
            String duration = annotation.duration();
            if (Ut.isNotNil(config) && Ut.isNotNil(config.getString("duration"))) {
                duration = config.getString("duration");
            }
            if (Ut.isNotNil(duration)) {
                final Duration durationAt = R2MO.toDuration(duration);
                if (Objects.nonNull(durationAt)) {
                    timer.configure(durationAt);
                }
            }
        }
        /* 4. 解析公式 */
        this.configureFormula(mission, timer, annotation, config);

        mission.scheduler(timer);
    }

    private void configureFormula(final Mission mission, final KScheduler timer, final Job annotation, final JsonObject config) {
        String runFormula = annotation.formula();
        if (EmService.JobType.FORMULA == mission.getType()) {
            /*
             * 4. 公式处理 (Formula)
             * 配置优先模式：
             * 1) 优先读取配置文件中的 formula
             * 2) 如果配置文件未设置，使用注解的配置
             */
            if (Ut.isNotNil(config) && Ut.isNotNil(config.getString("formula"))) {
                runFormula = config.getString("formula");
            }
        }
        // Error-60054 Detect
        mission.detectPre(runFormula);
        timer.configure(runFormula, null);
    }

    private JsonObject configuration(final Job annotation) {
        /* 读取配置 */
        final String config = annotation.config();
        if (Ut.isNotNil(config)) {
            return Ut.ioJObject(this.resolve(config));
        }
        return null;
    }

    private Mission mission(final JsonObject config) {
        if (Ut.isNotNil(config)) {
            /*
             * 移除以下字段：
             * - type
             * */
            final JsonObject json = config.copy();
            json.remove(KName.TYPE);
            final Mission mission = Ut.deserialize(json, Mission.class);
            return Objects.isNull(mission) ? new Mission() : mission;
        }
        return new Mission();
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
