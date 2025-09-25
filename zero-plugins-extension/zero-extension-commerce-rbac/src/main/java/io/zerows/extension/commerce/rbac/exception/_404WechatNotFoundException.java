package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author lang : 2024-07-12
 */
public class _404WechatNotFoundException extends WebException {
    public _404WechatNotFoundException(final Class<?> target, final String wechat) {
        super(target, wechat);
    }

    @Override
    public int getCode() {
        return -80228;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
