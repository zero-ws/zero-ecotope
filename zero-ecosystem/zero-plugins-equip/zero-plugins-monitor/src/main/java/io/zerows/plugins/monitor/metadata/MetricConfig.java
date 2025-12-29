package io.zerows.plugins.monitor.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lang : 2025-12-29
 */
@Data
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class MetricConfig extends MetricBase {

    @Override
    protected MetricType category() {
        return MetricType.CONFIG;
    }
}
