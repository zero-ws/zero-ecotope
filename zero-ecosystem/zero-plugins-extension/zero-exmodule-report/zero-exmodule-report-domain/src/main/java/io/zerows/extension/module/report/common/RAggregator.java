package io.zerows.extension.module.report.common;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.em.EmDim;
import io.zerows.support.Ut;

import java.io.Serializable;

/**
 * @author lang : 2024-10-30
 */
public class RAggregator implements Serializable {

    private final String field;
    private final EmDim.Aggregator aggregator;

    public RAggregator(final JsonObject configJ) {
        this.field = Ut.valueString(configJ, KName.SOURCE);
        this.aggregator = Ut.toEnum(() -> Ut.valueString(configJ, "function"),
            EmDim.Aggregator.class, EmDim.Aggregator.COUNT);
    }

    public String field() {
        return this.field;
    }

    public EmDim.Aggregator aggregator() {
        return this.aggregator;
    }
}
