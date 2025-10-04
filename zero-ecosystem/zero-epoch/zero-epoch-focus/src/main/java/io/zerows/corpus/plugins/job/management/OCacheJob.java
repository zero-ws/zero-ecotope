package io.zerows.corpus.plugins.job.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.corpus.plugins.job.metadata.Mission;
import io.zerows.sdk.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-04-30
 */
public interface OCacheJob extends OCache<Set<Mission>> {
    Cc<String, OCacheJob> CC_SKELETON = Cc.open();

    static OCacheJob of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheJobAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheJobAmbiguity(bundle), cacheKey);
    }

    static OCacheJob of() {
        return of(null);
    }

    static Set<Mission> entireValue() {
        return CC_SKELETON.get().values().stream()
            .flatMap(cache -> cache.value().stream())
            .collect(Collectors.toSet());
    }
}
