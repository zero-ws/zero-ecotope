package io.zerows.core.exception.boot;

import io.zerows.ams.annotations.Development;
import io.zerows.core.exception.BootingException;

/**
 * @author lang : 2023-06-07
 */
public class DuplicateRegistryException extends BootingException {

    public DuplicateRegistryException(final Class<?> clazz,
                                      final int size) {
        super(clazz, String.valueOf(size));
    }

    @Override
    public int getCode() {
        return -40104;
    }

    @Development
    private int _40104() {
        return this.getCode();
    }
}
