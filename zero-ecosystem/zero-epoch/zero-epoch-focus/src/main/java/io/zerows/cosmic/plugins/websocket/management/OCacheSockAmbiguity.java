package io.zerows.cosmic.plugins.websocket.management;

import io.zerows.cosmic.plugins.websocket.Remind;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
class OCacheSockAmbiguity extends AbstractAmbiguity implements OCacheSock {
    private final Set<Remind> socks = new HashSet<>();

    OCacheSockAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<Remind> value() {
        return this.socks;
    }

    @Override
    public OCacheSock add(final Set<Remind> socks) {
        this.socks.addAll(socks);
        return this;
    }

    @Override
    public OCacheSock remove(final Set<Remind> socks) {
        this.socks.removeAll(socks);
        return this;
    }
}
