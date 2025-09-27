package io.zerows.module.configuration.uca.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.module.configuration.atom.option.ClusterOptions;
import io.zerows.module.configuration.zdk.Transformer;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class ClusterTransformer implements Transformer<ClusterOptions> {
    @Override
    public ClusterOptions transform(final JsonObject config) {
        if (Objects.isNull(config)) {
            return new ClusterOptions();
        }
        return new ClusterOptions(config);
    }
}
