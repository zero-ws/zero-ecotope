package io.zerows.epoch.component.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.configuration.option.RpcOptions;
import io.zerows.epoch.sdk.options.Transformer;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class RpcTransformer implements Transformer<RpcOptions> {

    @Override
    public RpcOptions transform(final JsonObject input) {
        if (Objects.isNull(input)) {
            return new RpcOptions();
        }
        return new RpcOptions(input);
    }
}
