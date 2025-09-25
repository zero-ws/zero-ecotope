package io.zerows.module.metadata.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _412ContractFieldException extends WebException {
    public _412ContractFieldException(final Class<?> clazz,
                                      final Class<?> fieldType,
                                      final Class<?> target,
                                      final Integer times) {
        super(clazz, fieldType, target, times);
    }

    @Override
    public int getCode() {
        return -60040;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.PRECONDITION_FAILED;
    }
}
