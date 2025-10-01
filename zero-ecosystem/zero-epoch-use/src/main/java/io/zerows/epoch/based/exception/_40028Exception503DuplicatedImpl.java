package io.zerows.epoch.based.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _40028Exception503DuplicatedImpl extends VertxBootException {
    public _40028Exception503DuplicatedImpl(final Class<?> interfaceCls) {
        super(ERR._40028, Objects.requireNonNull(interfaceCls).getName());
    }
}
