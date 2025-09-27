package io.zerows.core.web.container.exception;

import io.zerows.ams.constant.em.app.ServerType;
import io.zerows.core.exception.BootingException;
import io.zerows.core.util.Ut;

import java.util.Set;

public class BootAgentDuplicatedException extends BootingException {

    public BootAgentDuplicatedException(final Class<?> clazz,
                                        final ServerType type,
                                        final int numbber,
                                        final Set<String> agents) {
        super(clazz, numbber, type, Ut.fromJoin(agents));
    }

    @Override
    public int getCode() {
        return -40004;
    }
}
