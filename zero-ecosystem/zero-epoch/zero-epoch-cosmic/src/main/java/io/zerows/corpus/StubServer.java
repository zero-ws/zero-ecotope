package io.zerows.corpus;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.corpus.model.running.RunVertx;
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
    Set<RunServer> createAsync(RunVertx runVertx);
}
