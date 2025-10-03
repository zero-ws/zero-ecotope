package io.zerows.component.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.option.ClusterOptions;
import io.zerows.sdk.environment.Transformer;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TransformerCluster implements Transformer<ClusterOptions> {
    @Override
    public ClusterOptions transform(final JsonObject config) {
        if (Objects.isNull(config)) {
            return new ClusterOptions();
        }
        return new ClusterOptions(config);
    }
}
