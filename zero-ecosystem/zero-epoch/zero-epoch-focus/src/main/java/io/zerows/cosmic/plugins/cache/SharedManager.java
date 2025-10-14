package io.zerows.cosmic.plugins.cache;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.r2mo.typed.cc.Cc;

import java.util.function.Supplier;

/**
 * @author lang : 2025-10-15
 */
class SharedManager {
    private static final Cc<String, SharedClient> CC_INSTANCE = Cc.open();

    private static final SharedManager INSTANCE = new SharedManager();

    private SharedManager() {
    }

    static SharedManager of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    public SharedManager putClient(final String name, final SharedClient client) {
        CC_INSTANCE.put(name, client);
        return this;
    }

    public SharedManager removeClient(final String name) {
        CC_INSTANCE.remove(name);
        return this;
    }

    public SharedClient getClient(final String name) {
        return CC_INSTANCE.get(name);
    }

    public SharedClient getClient(final String name, final Supplier<SharedClient> clientSupplier) {
        return CC_INSTANCE.pick(clientSupplier, name);
    }
}
