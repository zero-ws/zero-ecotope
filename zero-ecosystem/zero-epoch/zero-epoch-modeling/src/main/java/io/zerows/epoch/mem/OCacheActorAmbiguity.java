package io.zerows.epoch.mem;

import io.zerows.epoch.corpus.model.action.OActorComponent;
import io.zerows.epoch.sdk.osgi.AbstractAmbiguity;
import io.zerows.epoch.sdk.management.OCache;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
class OCacheActorAmbiguity extends AbstractAmbiguity implements OCacheActor {

    final static OActorComponent ENTIRE_ACTOR = new OActorComponent();

    private final OActorComponent actor = new OActorComponent();

    OCacheActorAmbiguity(final Bundle bundle) {
        super(bundle);
    }


    @Override
    public OActorComponent value() {
        return this.actor;
    }

    @Override
    public OCache<OActorComponent> add(final OActorComponent actor) {
        this.actor.add(actor);

        ENTIRE_ACTOR.add(actor);
        return this;
    }

    @Override
    public OCache<OActorComponent> remove(final OActorComponent actor) {
        this.actor.remove(actor);

        ENTIRE_ACTOR.remove(actor);
        return this;
    }
}
