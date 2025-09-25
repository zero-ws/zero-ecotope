package io.zerows.core.web.container.uca.server;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.running.RunServer;
import io.zerows.core.web.model.atom.running.RunVertx;
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
