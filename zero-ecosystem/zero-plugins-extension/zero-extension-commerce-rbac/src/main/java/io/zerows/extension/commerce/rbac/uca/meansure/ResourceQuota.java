package io.zerows.extension.commerce.rbac.uca.meansure;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.Status;
import io.zerows.corpus.plugins.QuotaBase;
import io.zerows.extension.commerce.rbac.eon.ScConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ResourceQuota extends QuotaBase {
    public ResourceQuota(final Vertx vertx) {
        super(vertx);
    }

    @Override
    public void handle(final Promise<Status> event) {
        // Permission Pool
        this.mapAsync(ScConstant.POOL_PERMISSIONS, map -> {
            System.out.println(map);
        });
    }
}
