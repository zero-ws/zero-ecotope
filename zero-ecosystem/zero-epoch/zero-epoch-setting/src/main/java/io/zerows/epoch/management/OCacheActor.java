package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.WebActor;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheActor extends OCache<WebActor> {
    Cc<String, OCacheActor> CC_SKELETON = Cc.open();

    static OCacheActor of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheActorAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheActorAmbiguity(bundle), cacheKey);
    }

    static OCacheActor of() {
        return of(null);
    }

    static WebActor entireValue() {
        return OCacheActorAmbiguity.ENTIRE_ACTOR;
    }

    interface Tool {

        static void addTo(final Set<WebEvent> events) {
            events.stream().filter(Objects::nonNull)
                /* Only Uri Pattern will be extracted to URI_PATHS */
                .filter(item -> 0 < item.getPath().indexOf(":"))
                .forEach(item -> OCacheUri.Tool.resolve(item.getPath(), item.getMethod()));
            final long size = OCacheUri.entireUri().size();
            Ut.Log.metadata(OCacheActor.class).info("( Uri ) Pattern Uri Size: {0}", String.valueOf(size));
        }
    }
}
