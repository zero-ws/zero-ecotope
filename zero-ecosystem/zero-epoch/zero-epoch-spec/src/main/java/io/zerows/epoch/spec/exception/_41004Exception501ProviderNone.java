package io.zerows.epoch.spec.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.epoch.configuration.ConfigProvider;

public class _41004Exception501ProviderNone extends VertxBootException {

    public _41004Exception501ProviderNone(final String selected) {
        super(ERR._41004, ConfigProvider.name(selected));
    }
}
