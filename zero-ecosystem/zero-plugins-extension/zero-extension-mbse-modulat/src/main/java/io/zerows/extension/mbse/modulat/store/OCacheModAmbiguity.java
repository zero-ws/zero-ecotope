package io.zerows.extension.mbse.modulat.store;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.atom.MultiKeyMap;
import io.zerows.epoch.corpus.metadata.zdk.AbstractAmbiguity;
import io.zerows.specification.access.app.HMod;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-07-08
 */
class OCacheModAmbiguity extends AbstractAmbiguity implements OCacheMod {
    private final MultiKeyMap<HMod> STORED = new MultiKeyMap<>();

    OCacheModAmbiguity(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public Set<String> keys() {
        return this.STORED.keySet();
    }

    @Override
    public HMod valueGet(final String key) {
        return this.STORED.getOr(key);
    }

    @Override
    public OCacheMod add(final HMod mod) {
        Objects.requireNonNull(mod);
        final String modId = mod.id();
        if (Ut.isNotNil(modId)) {
            this.STORED.put(modId, mod, mod.name());
        }
        return this;
    }

    @Override
    public OCacheMod remove(final HMod mod) {
        Objects.requireNonNull(mod);
        final String modId = mod.id();
        if (Ut.isNotNil(modId)) {
            this.STORED.remove(modId);
        }
        return this;
    }
}
