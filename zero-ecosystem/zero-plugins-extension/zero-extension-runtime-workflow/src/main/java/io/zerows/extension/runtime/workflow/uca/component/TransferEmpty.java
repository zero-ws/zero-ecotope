package io.zerows.extension.runtime.workflow.uca.component;

import io.vertx.core.Future;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.uca.central.AbstractMovement;
import io.zerows.program.Ux;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

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
