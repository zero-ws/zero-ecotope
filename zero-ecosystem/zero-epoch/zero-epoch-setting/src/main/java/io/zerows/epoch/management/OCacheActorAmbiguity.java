package io.zerows.epoch.management;

import io.zerows.epoch.basicore.ActorComponent;
import io.zerows.sdk.management.OCache;
import io.zerows.sdk.management.AbstractAmbiguity;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
class OCacheActorAmbiguity extends AbstractAmbiguity implements OCacheActor {

    final static ActorComponent ENTIRE_ACTOR = new ActorComponent();

    private final ActorComponent actor = new ActorComponent();

    OCacheActorAmbiguity(final Bundle bundle) {
        super(bundle);
    }


    @Override
    public ActorComponent value() {
        return this.actor;
    }

    @Override
    public OCache<ActorComponent> add(final ActorComponent actor) {
        this.actor.add(actor);

        ENTIRE_ACTOR.add(actor);
        return this;
    }

    @Override
    public OCache<ActorComponent> remove(final ActorComponent actor) {
        this.actor.remove(actor);

        ENTIRE_ACTOR.remove(actor);
        return this;
    }
}
