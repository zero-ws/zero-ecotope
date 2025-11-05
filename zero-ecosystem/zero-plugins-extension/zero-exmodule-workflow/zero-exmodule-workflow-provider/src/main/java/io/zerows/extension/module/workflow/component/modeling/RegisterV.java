package io.zerows.extension.module.workflow.component.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.metadata.MetaInstance;
import io.zerows.program.Ux;

import static io.zerows.extension.module.workflow.boot.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RegisterV extends AbstractRegister {
    @Override
    public Future<JsonObject> insertAsync(final JsonObject params, final MetaInstance metadata) {
        LOG.Move.info(this.getClass(), "`virtual` configured to true");
        return Ux.future(params);
    }

    @Override
    public Future<JsonObject> updateAsync(final JsonObject params, final MetaInstance metadata) {
        LOG.Move.info(this.getClass(), "`virtual` configured to true");
        final Register register = Register.instance(params);
        return register.updateAsync(params, metadata);
    }

    @Override
    public Future<JsonObject> saveAsync(final JsonObject params, final MetaInstance metadata) {
        LOG.Move.info(this.getClass(), "`virtual` configured to true");
        final Register register = Register.instance(params);
        return register.saveAsync(params, metadata);
    }
}
