package io.zerows.extension.crud.boot;

import io.zerows.extension.crud.common.IxConfig;

/**
 * @author lang : 2025-12-24
 */
public abstract class IxSetupBase<T> implements IxSetup<T> {
    private final IxConfig config;

    protected IxSetupBase(final IxConfig config) {
        this.config = config;
    }

    protected IxConfig config() {
        return this.config;
    }
}
