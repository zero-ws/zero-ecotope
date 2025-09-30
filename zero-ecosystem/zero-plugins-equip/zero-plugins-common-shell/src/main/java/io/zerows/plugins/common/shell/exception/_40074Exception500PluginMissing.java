package io.zerows.plugins.common.shell.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40074Exception500PluginMissing extends VertxBootException {
    public _40074Exception500PluginMissing(final String plugin) {
        super(ERR._40074, plugin);
    }
}
