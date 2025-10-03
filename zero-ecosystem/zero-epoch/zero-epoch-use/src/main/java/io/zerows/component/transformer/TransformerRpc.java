package io.zerows.component.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.option.RpcOptions;
import io.zerows.sdk.environment.Transformer;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TransformerRpc implements Transformer<RpcOptions> {

    @Override
    public RpcOptions transform(final JsonObject input) {
        if (Objects.isNull(input)) {
            return new RpcOptions();
        }
        return new RpcOptions(input);
    }
}
