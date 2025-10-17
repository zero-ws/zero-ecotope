package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.Getter;

import java.util.Objects;

/**
 * @author lang : 2025-10-17
 */
public class JobClientManager extends AddOnManager<JobClient> {
    private static final Cc<String, JobClient> CC_STORED = Cc.open();

    private static final JobClientManager INSTANCE = new JobClientManager();

    @Getter
    private JobConfig configuration;
    @Getter
    private JobStore store;
    @Getter
    private Interval interval;

    private JobClientManager() {
    }

    static JobClientManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, JobClient> stored() {
        return CC_STORED;
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
        // 初始化存储器
        this.store = this.configuration.createStore();
        this.interval = this.configuration.createInterval();
    }
}
