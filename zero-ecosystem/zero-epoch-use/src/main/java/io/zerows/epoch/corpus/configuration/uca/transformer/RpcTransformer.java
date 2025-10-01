package io.zerows.epoch.corpus.configuration.uca.transformer;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.configuration.atom.option.RpcOptions;
import io.zerows.epoch.corpus.configuration.zdk.Transformer;

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
