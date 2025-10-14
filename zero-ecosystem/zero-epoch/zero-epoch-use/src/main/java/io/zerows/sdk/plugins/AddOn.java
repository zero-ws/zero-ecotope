package io.zerows.sdk.plugins;

import com.google.inject.Key;

import java.util.UUID;

/**
 * @author lang : 2025-10-14
 */
public interface AddOn<DI> {

    Key<DI> getKey();

    DI createSingleton();

    default DI createInstance() {
        return this.createInstance(UUID.randomUUID().toString());
    }

    DI createInstance(String name);
}
