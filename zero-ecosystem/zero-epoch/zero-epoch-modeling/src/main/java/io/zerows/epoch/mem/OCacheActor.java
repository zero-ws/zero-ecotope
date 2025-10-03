package io.zerows.epoch.mem;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.corpus.model.action.OActorComponent;
import io.zerows.support.Ut;
import io.zerows.epoch.sdk.management.OCache;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheActor extends OCache<OActorComponent> {
    Cc<String, OCacheActor> CC_SKELETON = Cc.open();

    static OCacheActor of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheActorAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheActorAmbiguity(bundle), cacheKey);
    }

    static OCacheActor of() {
        return of(null);
    }

    static OActorComponent entireValue() {
        return OCacheActorAmbiguity.ENTIRE_ACTOR;
    }

    interface Tool {

        static void addTo(final Set<Event> events) {
            events.stream().filter(Objects::nonNull)
                /* Only Uri Pattern will be extracted to URI_PATHS */
                .filter(item -> 0 < item.getPath().indexOf(":"))
                .forEach(item -> OCacheUri.Tool.resolve(item.getPath(), item.getMethod()));
            final long size = OCacheUri.entireUri().size();
            Ut.Log.metadata(OCacheActor.class).info("( Uri ) Pattern Uri Size: {0}", String.valueOf(size));
        }
    }
}
