package io.zerows.extension.commerce.rbac.osgi.spi;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.agent.service.accredit.ActionService;
import io.zerows.extension.commerce.rbac.agent.service.accredit.ActionStub;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.exception._80209Exception404ActionMissing;
import io.zerows.extension.runtime.skeleton.osgi.spi.ui.Anchoret;
import io.zerows.extension.runtime.skeleton.osgi.spi.web.Seeker;
import io.zerows.module.domain.atom.typed.UObject;

import java.util.Objects;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/*
 * Seek impact resource for params here, it will be passed by crud
 */
public class ExIntimitySeeker extends Anchoret<Seeker> implements Seeker {

    private transient final ActionStub stub = Ut.singleton(ActionService.class);

    @Override
    public Future<JsonObject> fetchImpact(final JsonObject params) {
        /*
         * Uri, Method
         */
        final String uri = params.getString(Seeker.ARG0);
        final HttpMethod method = HttpMethod.valueOf(params.getString(Seeker.ARG1));
        final String sigma = params.getString(Seeker.ARG2);
        LOG.Resource.info(this.getLogger(), AuthMsg.SEEKER_RESOURCE, uri, method, sigma);
        return this.stub.fetchAction(uri, method, sigma).compose(action -> Objects.isNull(action) ?
            FnVertx.failOut(_80209Exception404ActionMissing.class, method + " " + uri) :
            UObject.create(params).append(KName.RESOURCE_ID, action.getResourceId())
                .toFuture());
    }
}
