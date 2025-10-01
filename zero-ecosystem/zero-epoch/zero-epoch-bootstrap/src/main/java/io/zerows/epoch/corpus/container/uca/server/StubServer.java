package io.zerows.epoch.corpus.container.uca.server;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author lang : 2024-05-03
 */
public interface StubServer {
    Cc<String, StubServer> CCT_SKELETON = Cc.openThread();

    static StubServer of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, StubServerService.class);
        return CCT_SKELETON.pick(() -> new StubServerService(bundle), cacheKey);
    }

    static StubServer of() {
        return of(null);
    }

    // --------------------- 行为专用 ---------------------
    Set<RunServer> createAsync(RunVertx runVertx);
}
