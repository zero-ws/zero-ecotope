package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _403PermissionLimitException extends WebException {

    public _403PermissionLimitException(final Class<?> clazz,
                                        final String actionCode,
                                        final String permissionId) {
        super(clazz, actionCode, permissionId);
    }

    @Override
    public int getCode() {
        return -80213;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.FORBIDDEN;
    }
}
