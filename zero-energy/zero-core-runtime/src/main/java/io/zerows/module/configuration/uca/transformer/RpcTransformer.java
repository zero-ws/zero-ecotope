package io.zerows.module.configuration.uca.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.module.configuration.atom.option.RpcOptions;
import io.zerows.module.configuration.zdk.Transformer;

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
