package io.zerows.extension.runtime.workflow.uca.coadjutor;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.configuration.MetaInstance;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.uca.central.AbstractMovement;
import io.zerows.extension.runtime.workflow.uca.modeling.Register;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class StaySave extends AbstractMovement implements Stay {
    @Override
    public Future<WRecord> keepAsync(final WRequest request, final WTransition wTransition) {
        // Todo Updating
        final JsonObject params = request.request();
        return this.updateAsync(params, wTransition).compose(record -> {
            final MetaInstance metadataOut = MetaInstance.output(record, this.metadataIn());
            // Record Updating
            final Register register = Register.phantom(params, metadataOut);
            return register.updateAsync(params, metadataOut)
                .compose(nil -> Ux.future(record));
        }).compose(record -> this.afterAsync(record, wTransition));
    }
}
