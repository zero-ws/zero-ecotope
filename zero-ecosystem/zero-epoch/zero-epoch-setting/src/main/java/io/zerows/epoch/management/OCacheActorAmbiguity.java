package io.zerows.epoch.management;

import io.zerows.epoch.basicore.WebActor;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-04-21
 */
class OCacheActorAmbiguity extends AbstractAmbiguity implements OCacheActor {

    final static WebActor ENTIRE_ACTOR = new WebActor();

    private final WebActor actor = new WebActor();

    OCacheActorAmbiguity(final HBundle bundle) {
        super(bundle);
    }


    @Override
    public WebActor value() {
        return this.actor;
    }

    @Override
    public OCache<WebActor> add(final WebActor actor) {
        this.actor.add(actor);

        ENTIRE_ACTOR.add(actor);
        return this;
    }

    @Override
    public OCache<WebActor> remove(final WebActor actor) {
        this.actor.remove(actor);

        ENTIRE_ACTOR.remove(actor);
        return this;
    }
}
