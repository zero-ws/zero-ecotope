package io.zerows.module.metadata.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _10005Exception500PluginDynamic extends VertxBootException {

    public _10005Exception500PluginDynamic(final String key, final String config) {
        super(ERR._10005, key, config);
    }
}
