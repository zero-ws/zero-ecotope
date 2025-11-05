package io.zerows.extension.module.workflow.component.component;

import io.vertx.core.Future;
import io.zerows.extension.module.workflow.component.central.Behaviour;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Movement extends Behaviour {

    Future<WTransition> moveAsync(WRequest request);
}
