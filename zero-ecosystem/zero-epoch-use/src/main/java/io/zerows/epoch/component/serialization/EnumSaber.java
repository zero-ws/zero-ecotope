package io.zerows.epoch.component.serialization;

import io.zerows.epoch.program.Ut;

/**
 * Enum
 */
class EnumSaber extends AbstractSaber {

    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (input instanceof Enum) {
            reference = Ut.invoke(input, "name");
        }
        return reference;
    }
}
