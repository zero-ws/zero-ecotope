package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80228Exception404WechatNotFound extends VertxWebException {
    public _80228Exception404WechatNotFound(final String wechat) {
        super(ERR._80228, wechat);
    }
}
