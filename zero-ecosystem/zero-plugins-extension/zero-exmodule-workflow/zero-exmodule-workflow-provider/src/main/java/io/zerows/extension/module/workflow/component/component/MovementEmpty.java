package io.zerows.extension.module.workflow.component.component;

import io.vertx.core.Future;
import io.zerows.extension.module.workflow.component.central.AbstractTransfer;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.program.Ux;

import static io.zerows.extension.module.workflow.boot.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MovementEmpty extends AbstractTransfer implements Movement {
    @Override
    public Future<WTransition> moveAsync(final WRequest request) {
        LOG.Move.warn(this.getClass(), "[ Empty ] `Movement` component has not been configured. ");
        return Ux.future();
    }
}
