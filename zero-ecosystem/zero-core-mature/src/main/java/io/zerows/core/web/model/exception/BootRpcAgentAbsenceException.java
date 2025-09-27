package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootRpcAgentAbsenceException extends BootingException {

    public BootRpcAgentAbsenceException(final Class<?> clazz,
                                        final Class<?> interfaceCls) {
        super(clazz, interfaceCls);
    }

    @Override
    public int getCode() {
        return -40048;
    }
}
