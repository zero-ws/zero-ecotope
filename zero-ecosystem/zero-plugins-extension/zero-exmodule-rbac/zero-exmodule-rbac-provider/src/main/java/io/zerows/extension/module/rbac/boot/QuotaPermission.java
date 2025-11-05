package io.zerows.extension.module.rbac.boot;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.Status;
import io.zerows.cosmic.plugins.QuotaBase;
import io.zerows.extension.module.rbac.common.ScConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class QuotaPermission extends QuotaBase {
    public QuotaPermission(final Vertx vertx) {
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
