package io.zerows.cortex.metadata;

import io.vertx.ext.auth.User;
import io.zerows.support.Ut;

class ParameterPre {
    static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || Ut.isImplement(paramType, expected);
    }

    @SuppressWarnings("all")
    static boolean allowNull(final Class<?> paramType) {
        if (is(paramType, User.class)) {
            return true;
        }
        return false;
    }
}
