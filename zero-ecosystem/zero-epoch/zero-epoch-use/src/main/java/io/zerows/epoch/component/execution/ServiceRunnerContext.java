package io.zerows.epoch.component.execution;

import io.zerows.epoch.corpus.metadata.service.CallbackParameter;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-07-02
 */
class ServiceRunnerContext extends ServiceRunnerBase {
    ServiceRunnerContext(final Bundle owner) {
        super(owner);
    }

    @Override
    public void start(final CallbackParameter parameter) {
        // Context 上线，上线之后没有任何服务记录，所以读取所有生产者和消费者
    }

    @Override
    public void stop(final CallbackParameter parameter) {

    }
}
