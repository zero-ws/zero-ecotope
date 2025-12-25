package io.zerows.extension.crud.boot;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.extension.skeleton.underway.Primed;

import java.util.Set;

/**
 * @author lang : 2025-12-25
 */
@SPID(priority = 1024)
public class PrimedCRUD implements Primed {
    @Override
    public Future<Boolean> afterAsync(final Set<MDConfiguration> waitSet) {
        /*
         * 带有 Overwrite 的执行处理流程
         */
        MDCRUDManager.of().handleAfter(waitSet);

        return Future.succeededFuture(Boolean.TRUE);
    }
}
