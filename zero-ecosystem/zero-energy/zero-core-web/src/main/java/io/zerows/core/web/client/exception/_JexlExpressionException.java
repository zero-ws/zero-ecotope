package io.zerows.core.web.client.exception;

import io.zerows.core.exception.InternalException;
import io.zerows.core.util.Ut;

public class _JexlExpressionException extends InternalException {

    private static final String MESSAGE = "The expression \"{0}\" could not be parsed, details = {1}";

    public _JexlExpressionException(final Class<?> caller,
                                    final String expression,
                                    final Throwable ex) {
        super(caller, Ut.fromMessage(MESSAGE, expression, ex.getMessage()));
    }

    @Override
    public int getCode() {
        return -15002;
    }
}
