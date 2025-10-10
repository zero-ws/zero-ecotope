package io.zerows.cosmic;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.metadata.RunServerLegacy;
import io.zerows.cortex.metadata.RunVertxLegacy;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Set;

/**
 * @author lang : 2024-05-03
 */
public interface StubServer {
    Cc<String, StubServer> CCT_SKELETON = Cc.openThread();

    static StubServer of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, StubServerService.class);
        return CCT_SKELETON.pick(() -> new StubServerService(bundle), cacheKey);
    }

    static StubServer of() {
        return of(null);
    }

    // --------------------- 行为专用 ---------------------
    Set<RunServerLegacy> createAsync(RunVertxLegacy runVertxLegacy);
}
