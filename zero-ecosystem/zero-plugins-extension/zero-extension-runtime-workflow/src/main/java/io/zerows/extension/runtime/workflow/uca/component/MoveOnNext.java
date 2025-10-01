package io.zerows.extension.runtime.workflow.uca.component;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.exception._80605Exception409InValidInstance;
import io.zerows.extension.runtime.workflow.uca.camunda.RunOn;
import io.zerows.extension.runtime.workflow.uca.central.AbstractMoveOn;
import io.zerows.epoch.corpus.domain.atom.specification.KFlow;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MoveOnNext extends AbstractMoveOn {
    @Override
    public Future<WTransition> moveAsync(final WRequest request, final WTransition wTransition) {
        final ProcessInstance instance = wTransition.instance();
        final KFlow key = request.workflow();
        final String instanceId = key.instanceId();
        if (Objects.isNull(instance)) {
            return FnVertx.failOut(_80605Exception409InValidInstance.class, instanceId);
        }
        return wTransition.start().compose(started -> {
            final JsonObject parameters = wTransition.moveParameter(request);
            final RunOn runOn = RunOn.get();
            return runOn.moveAsync(parameters, wTransition);
        }).compose(wTransition::end);
    }
}
