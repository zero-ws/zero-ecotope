package io.zerows.extension.module.workflow.component.component;

import io.vertx.core.Future;
import io.zerows.extension.module.workflow.component.central.Behaviour;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;

/**
 * Todo Generation
 * 1. Start Component
 * 2. Generate Component
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Transfer extends Behaviour {

    Future<WRecord> moveAsync(WRequest request, WTransition wTransition);
}
