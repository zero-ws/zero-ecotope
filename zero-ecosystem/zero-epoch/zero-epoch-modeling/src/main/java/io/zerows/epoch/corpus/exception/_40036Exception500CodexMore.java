package io.zerows.epoch.corpus.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _40036Exception500CodexMore extends VertxBootException {
    public _40036Exception500CodexMore(final Class<?> target) {
        super(ERR._40036, Objects.requireNonNull(target).getName());
    }
}
