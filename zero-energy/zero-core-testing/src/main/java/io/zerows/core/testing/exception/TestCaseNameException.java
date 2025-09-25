package io.zerows.core.testing.exception;

import io.zerows.core.exception.BootingException;

/*
 * Zero Quiz needed when you used QzTc / QzMicroTc
 * It means that all the mock case name must end with "Tc"
 * Because the name will be parsed.
 */
public class TestCaseNameException extends BootingException {

    public TestCaseNameException(final Class<?> clazz,
                                 final String caseName) {
        super(clazz, caseName);
    }

    @Override
    public int getCode() {
        return -40062;
    }
}
