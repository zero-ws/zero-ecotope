package io.zerows.cosmic;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.specification.development.compiled.HBundle;


/**
 * @author lang : 2024-05-03
 */
public interface StubServer {
    Cc<String, StubServer> CCT_SKELETON = Cc.open();

    static StubServer of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, StubServerService.class);
        return CCT_SKELETON.pick(() -> new StubServerService(bundle), cacheKey);
    }

    static StubServer of() {
        return of(null);
    }

    // --------------------- 行为专用 ---------------------
    Future<RunServer> createAsync(RunVertx runVertx);
}
