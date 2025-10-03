package io.zerows.extension.commerce.rbac.plugins.request;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.io.plugins.extension.AbstractRegion;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.uca.acl.region.CommonCosmo;
import io.zerows.extension.commerce.rbac.uca.acl.region.Cosmo;
import io.zerows.extension.commerce.rbac.uca.acl.region.SeekCosmo;
import io.zerows.extension.commerce.rbac.util.Sc;

/*
 * Extension in RBAC module
 * 1) Region calculation
 * 2) Visitant calculation ( Extension More )
 */
public class DataRegion extends AbstractRegion {
    private static final Cc<String, Cosmo> CC_COSMO = Cc.openThread();

    @Override
    public Future<Envelop> before(final RoutingContext context, final Envelop envelop) {
        if (!this.isEnabled(context)) {
            // Data Region disabled
            return Ux.future(envelop);
        }

        /* Get Critical parameters */
        return Sc.cacheView(context, envelop.habitus()).compose(matrix -> {
            if (this.isRegion(matrix)) {
                this.logger().info(AuthMsg.REGION_BEFORE, context.request().path(), matrix.encode());
                /*
                 * Select cosmo by matrix
                 */
                final Cosmo cosmo = this.cosmo(matrix);
                return cosmo.before(envelop, matrix);
            } else {
                /*
                 * Matrix null or empty
                 */
                return Ux.future(envelop);
            }
        }).otherwise(Ux.otherwise(envelop));
    }

    @Override
    public Future<Envelop> after(final RoutingContext context, final Envelop response) {
        if (!this.isEnabled(context)) {
            // Data Region disabled
            return Ux.future(response);
        }

        /* Get Critical parameters */
        return Sc.cacheView(context, response.habitus()).compose(matrix -> {
            if (this.isRegion(matrix)) {
                this.logger().info(AuthMsg.REGION_AFTER, matrix.encode());
                /*
                 * Select cosmo by matrix
                 */
                final Cosmo cosmo = this.cosmo(matrix);
                return cosmo.after(response, matrix);
            } else {
                /*
                 * Matrix null or empty
                 */
                return Ux.future(response);
            }
        }).otherwise(Ux.otherwise(response));
    }

    private Cosmo cosmo(final JsonObject matrix) {
        /* Build DataCosmo */
        if (matrix.containsKey(KName.SEEKER)) {
            /*
             * Virtual resource region calculation
             */
            return CC_COSMO.pick(SeekCosmo::new, SeekCosmo.class.getName());
        } else {
            /*
             * Actual resource region calculation
             */
            return CC_COSMO.pick(CommonCosmo::new, CommonCosmo.class.getName());
        }
    }
}
