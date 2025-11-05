package io.zerows.extension.module.workflow.component.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.metadata.MetaInstance;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RegisterN extends AbstractRegister {
    @Override
    public Future<JsonObject> insertAsync(final JsonObject params, final MetaInstance metadata) {
        return Ux.future(params);
    }

    @Override
    public Future<JsonObject> updateAsync(final JsonObject params, final MetaInstance metadata) {
        return Ux.future(params);
    }

    @Override
    public Future<JsonObject> saveAsync(final JsonObject params, final MetaInstance metadata) {
        return Ux.future(params);
    }
}
