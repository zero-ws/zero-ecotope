package io.zerows.cosmic.plugins.job.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.function.Fn;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.exception._60042Exception501JobOnMissing;
import io.zerows.cosmic.plugins.job.exception._60054Exception409JobFormulaError;
import io.zerows.epoch.annotations.Off;
import io.zerows.epoch.annotations.On;
import io.zerows.epoch.constant.KName;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmService;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Mission 数据对象，用于描述任务的详细信息并存储任务定义
 * <p>
 * 主要来源：
 * 1) 来自被扫描的 @Job 注解类（每个类应对应一个 Mission）
 * 2) 来自 JobStore 接口（任务定义也可能保存在数据库或其他持久化层）
 */
@Data
@Slf4j
public class Mission implements Serializable {
    /* 任务状态，默认状态为 STARTING */
    private EmService.JobStatus status = EmService.JobStatus.STARTING;
    /* 任务名称 */
    private String name;
    /* 任务类型 */
    private EmService.JobType type;
    /* 任务代码（唯一标识） */
    private String code;
    /* 任务描述 */
    private String comment;
    /* 是否只读（编程定义） */
    private boolean readOnly;
    /*
     * Worker 的超时时间阈值（用于 findRunning 场景）
     * 该参数会绑定到当前 Mission，用于设置后台 Worker 的超时，示例：
     *
     * final WorkerExecutor executor = this.vertx.createSharedWorkerExecutor(code, 1, threshold);
     *
     * 上述代码中，系统会为当前后台任务设置超时参数，这与调度无直接关系，故将相关逻辑从 KScheduler 移至 Mission。
     *
     * 两种来源：
     * 1) 编程定义（注解中）
     * 2) 配置项（如配置文件/持久化）
     *
     * - 默认时间单位为 TimeUnit.SECONDS
     * - 最终结果以纳秒（ns）保存
     *
     * 注意：该字段不可直接序列化，必须通过 timeout 方法来设置，否则 Worker 会使用默认参数。
     **/
    @JsonIgnore
    private long threshold = VValue.RANGE;
    /* 任务配置 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject metadata = new JsonObject();
    /* 任务附加信息 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject additional = new JsonObject();

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> income;

    private String incomeAddress;
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> outcome;

    private String outcomeAddress;

    /* 运行时引用（代理对象） */
    @JsonIgnore
    private Object proxy;
    /* 任务启动方法（@On 标注的方法） */
    @JsonIgnore
    private Method on;
    /* 任务结束方法（@Off 标注的方法，可选） */
    @JsonIgnore
    private Method off;
    /*
     * 用于描述应用范围（基于旧 KApp 规范），该属性示例结构如下：
     * {
     *     "name":      "application name",
     *     "ns":        "the default namespace",
     *     "language":  "the default language",
     *     "sigma":     "the uniform sigma identifier"
     * }
     *
     * 注意：该变量在 Zero 扩展框架中会被使用，编程方式下在 SVN Store 连接前可能为 null。
     */
    @JsonIgnore
    private HArk ark;

    @JsonIgnore
    private KScheduler scheduler;

    // -------------------- 临时绑定
    @JsonIgnore
    private Long timerId;

    @Fluent
    public Mission timerId(final Long timerId) {
        if (Objects.nonNull(timerId)) {
            this.timerId = timerId;
        }
        return this;
    }

    public long timerId() {
        return this.timerId;
    }

    // -----------------------------

    public Mission connect(final Class<?> clazz) {
        /*
         * 将 clazz 与当前 Mission 关联，设置以下信息：
         * 1. 代理实例 proxy
         *    - on
         *    - off
         * 2. 输入/输出类型与地址
         *    - income
         *    - incomeAddress
         *    - outcome
         *    - outcomeAddress
         */
        final Object proxy = Ut.singleton(clazz);
        if (Objects.nonNull(proxy)) {
            /*
             * proxy 已成功初始化
             * 注意：其他实例字段会在后续 mission 使用时绑定
             */
            this.proxy = proxy;
            /*
             * 查找 @On 方法
             * 该方法在类定义中必须存在，否则抛出异常
             */
            this.on = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(On.class))
                .findFirst().orElse(null);
            Fn.jvmKo(Objects.isNull(this.on), _60042Exception501JobOnMissing.class, clazz.getName());
            /*
             * 解析 @On 注解中的 income 与 address
             */
            final Annotation on = this.on.getAnnotation(On.class);
            this.incomeAddress = this.invoke(on, "address", this::getIncomeAddress);
            this.income = this.invoke(on, "income", this::getIncome);
            if (Ut.isNil(this.incomeAddress)) {
                this.incomeAddress = null;
            }

            /*
             * 查找 @Off 方法（可选）
             */
            this.off = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Off.class))
                .findFirst().orElse(null);
            if (Objects.nonNull(this.off)) {
                /*
                 * 解析 @Off 注解中的 outcome 与 address
                 */
                final Annotation out = this.off.getAnnotation(Off.class);
                this.outcomeAddress = this.invoke(out, "address", this::getOutcomeAddress);
                this.outcome = this.invoke(out, "outcome", this::getOutcome);
                if (Ut.isNil(this.outcomeAddress)) {
                    this.outcomeAddress = null;
                }
            }
            log.debug("[ ZERO ] ( Job ) 当前任务 `{}` 定义了 @Off 方法.", this.getCode());
        }
        return this;
    }

    private <T> T invoke(final Annotation annotation, final String annotationMethod,
                         final Supplier<T> supplier) {
        /*
         * 先尝试从持久化/配置中读取
         */
        T reference = supplier.get();
        if (Objects.isNull(reference)) {
            /*
             * 再从注解中提取值
             */
            reference = Ut.invoke(annotation, annotationMethod);
        }
        return reference;
    }

    public Mission timeout(final Duration timeoutAt) {
        if (Objects.isNull(timeoutAt)) {
            this.threshold = VValue.RANGE;
        } else {
            this.threshold = timeoutAt.toNanos();
        }
        return this;
    }

    public long timeout() {
        if (VValue.RANGE == this.threshold) {
            // 默认超时时间为 15 分钟
            return TimeUnit.MINUTES.toNanos(15);
        } else {
            return this.threshold;
        }
    }

    // ========================== KAppOld 信息 =======================
    public Mission ark(final HArk ark) {
        this.ark = ark;
        return this;
    }

    public HArk ark() {
        return this.ark;
    }

    public Mission scheduler(final KScheduler timer) {
        this.scheduler = timer;
        return this;
    }

    public KScheduler scheduler() {
        return this.scheduler;
    }

    // ========================== 确保配置合法性 =======================
    public void detectPre(final String formula) {
        if (EmService.JobType.FORMULA == this.type) {
            Fn.jvmKo(Ut.isNil(formula), _60054Exception409JobFormulaError.class, formula);
        }
    }

    public JsonObject mom() {
        final JsonObject monitorJ = new JsonObject();
        monitorJ.put(KName.STATUS, this.status.name());
        monitorJ.put(KName.READ_ONLY, this.readOnly);
        monitorJ.put(KName.TYPE, this.type.name());
        monitorJ.put(KName.COMMENT, this.comment);
        monitorJ.put("incomeAddr", this.incomeAddress);
        monitorJ.put("incomeComponent", Objects.nonNull(this.income) ? this.income.getName() : null);
        monitorJ.put("outcomeAddr", this.outcomeAddress);
        monitorJ.put("outcomeComponent", Objects.nonNull(this.outcome) ? this.outcome.getName() : null);
        monitorJ.put("onAction", this.on.getName());
        monitorJ.put("proxy", this.proxy.getClass().getName());
        monitorJ.put("offAction", Objects.nonNull(this.off) ? this.off.getName() : null);
        monitorJ.put("timer", Objects.nonNull(this.scheduler) ? this.scheduler.name() : null);
        monitorJ.put("threshold", TimeUnit.NANOSECONDS.toSeconds(this.timeout()) + "s");
        return monitorJ;
    }

    @Override
    public String toString() {
        return "Mission{" +
            "status=" + this.status +
            ", name='" + this.name + '\'' +
            ", readOnly='" + this.readOnly + '\'' +
            ", type=" + this.type +
            ", code='" + this.code + '\'' +
            ", comment='" + this.comment + '\'' +
            ", metadata=" + this.metadata +
            ", additional=" + this.additional +
            ", income=" + this.income +
            ", incomeAddress='" + this.incomeAddress + '\'' +
            ", outcome=" + this.outcome +
            ", outcomeAddress='" + this.outcomeAddress + '\'' +
            ", proxy=" + this.proxy +
            ", on=" + this.on +
            ", off=" + this.off +
            ", ark=" + this.ark +
            ", timer=" + this.scheduler +
            '}';
    }
}
