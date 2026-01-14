package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2025-10-17
 */
class JobClientManager extends AddOnManager<JobClient> {
    private static final Cc<String, JobClient> CC_CLIENT = Cc.open();

    private static final JobClientManager INSTANCE = new JobClientManager();

    private JobConfig configuration;
    private JobStore store;
    private JobInterval interval;

    private JobClientManager() {
    }

    static JobClientManager of() {
        return INSTANCE;
    }

    JobConfig refConfiguration() {
        return this.configuration;
    }

    JobInterval refInterval() {
        return this.interval;
    }

    JobStore refStore() {
        return this.store;
    }

    @Override
    protected Cc<String, JobClient> stored() {
        return CC_CLIENT;
    }

    @Override
    public void configure(final HConfig config) {
        if (Objects.isNull(config)) {
            return;
        }
        final JsonObject options = config.options();
        if (Ut.isNotNil(options)) {
            this.configuration = Ut.deserialize(options, JobConfig.class);
        }
        if (Objects.isNull(this.configuration)) {
            return;
        }
        // 任务存储初始化
        this.store = this.refStore(config);

        // 间隔调度初始化
        this.interval = this.refInterval(config);
    }

    private JobInterval refInterval(final HConfig config) {
        final Vertx vertxRef = config.ref();
        JobInterval interval = this.configuration.createInterval();
        if (Objects.isNull(interval)) {
            interval = new JobIntervalVertx();
            Ut.contract(interval, Vertx.class, vertxRef);
        }
        return interval;
    }

    private JobStore refStore(final HConfig config) {
        JobStore store = this.configuration.createStore();
        if (Objects.isNull(store)) {
            store = new JobStoreUnity();
        }
        // 调用初始化方法（填充 JobQueue）
        store.initialize();

        return store;
    }
}
