package io.zerows.weaver;

import io.zerows.support.Ut;

/**
 * Enum
 */
class SaberEnum extends SaberBase {

    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (input instanceof Enum) {
            reference = Ut.invoke(input, "name");
        }
        return reference;
    }
}
