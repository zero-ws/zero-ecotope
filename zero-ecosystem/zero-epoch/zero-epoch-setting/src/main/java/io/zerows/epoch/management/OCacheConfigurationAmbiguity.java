package io.zerows.epoch.management;

import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.configuration.MDId;
import io.zerows.sdk.management.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-07
 */
class OCacheConfigurationAmbiguity extends AbstractAmbiguity implements OCacheConfiguration {

    private final ConcurrentMap<String, MDConfiguration> moduleConfig = new ConcurrentHashMap<>();

    OCacheConfigurationAmbiguity(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public MDConfiguration valueGet(final String key) {
        Objects.requireNonNull(key);
        return this.moduleConfig.getOrDefault(key, null);
    }

    @Override
    public Set<MDConfiguration> valueSet() {
        return new HashSet<>(this.moduleConfig.values());
    }

    @Override
    public OCacheConfiguration add(final MDConfiguration MDConfiguration) {
        Objects.requireNonNull(MDConfiguration);
        final MDId id = MDConfiguration.id();
        this.moduleConfig.put(id.value(), MDConfiguration);
        return this;
    }

    @Override
    public OCacheConfiguration remove(final MDConfiguration MDConfiguration) {
        Objects.requireNonNull(MDConfiguration);
        final MDId id = MDConfiguration.id();
        this.moduleConfig.remove(id.value());
        return this;
    }
}
