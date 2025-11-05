package io.zerows.extension.module.workflow.component.component;

import io.vertx.core.Future;
import io.zerows.extension.module.workflow.component.central.AbstractMovement;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.program.Ux;

import static io.zerows.extension.module.workflow.boot.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TransferEmpty extends AbstractMovement implements Transfer {
    @Override
    public Future<WRecord> moveAsync(final WRequest request, final WTransition instance) {
        LOG.Move.warn(this.getClass(), "[ Empty ] `Transfer` component has not been configured. ");
        return Ux.future();
    }
}
