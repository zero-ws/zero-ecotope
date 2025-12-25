package io.zerows.extension.skeleton.boot;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.extension.skeleton.underway.Primed;

import java.util.Set;

/**
 * @author lang : 2025-12-25
 */
@SPID(priority = 1024)
public class PrimedOOB implements Primed {
    @Override
    public Future<Boolean> afterAsync(final Set<MDConfiguration> waitSet) {
        waitSet.stream()
            .map(MDConfiguration::inFiles)
            .forEach(DataIo.OOB_FILES::addAll);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
