package io.zerows.extension.module.workflow.component.coadjutor;

import io.vertx.core.Future;
import io.zerows.extension.module.workflow.component.central.Behaviour;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Stay extends Behaviour {

    Future<WRecord> keepAsync(WRequest request, WTransition instance);
}
