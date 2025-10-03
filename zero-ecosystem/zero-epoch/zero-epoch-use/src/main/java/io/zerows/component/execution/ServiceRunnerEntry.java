package io.zerows.component.execution;

import io.zerows.epoch.metadata.service.CallbackParameter;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-07-02
 */
class ServiceRunnerEntry extends ServiceRunnerBase {
    private final ServiceRunner consumer;
    private final ServiceRunner provider;
    private final ServiceRunner context;

    ServiceRunnerEntry(final Bundle owner) {
        super(owner);
        this.consumer = new ServiceRunnerConsumer(owner);
        this.provider = new ServiceRunnerProvider(owner);
        this.context = new ServiceRunnerContext(owner);
    }

    @Override
    public void start(final CallbackParameter parameter) {
        // 消费者堆叠
        if (parameter.isConsumer()) {
            this.consumer.start(parameter);
        }

        // 提供者堆叠
        if (parameter.isProvider()) {
            this.provider.start(parameter);
        }

        // 上下文堆叠
        this.context.start(parameter);
    }

    @Override
    public void stop(final CallbackParameter parameter) {

        // 消费者堆叠
        if (parameter.isConsumer()) {
            this.consumer.stop(parameter);
        }

        // 提供者堆叠
        if (parameter.isProvider()) {
            this.provider.stop(parameter);
        }

        // 上下文堆叠
        this.context.stop(parameter);
    }
}
