package io.zerows.extension.module.rbac.spi;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UObject;
import io.zerows.extension.module.rbac.serviceimpl.ActionService;
import io.zerows.extension.module.rbac.common.ScAuthMsg;
import io.zerows.extension.module.rbac.exception._80209Exception404ActionMissing;
import io.zerows.extension.module.rbac.servicespec.ActionStub;
import io.zerows.extension.skeleton.spi.ScSeeker;
import io.zerows.extension.skeleton.spi.UiAnchoret;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.module.rbac.boot.Sc.LOG;

/*
 * Seek impact resource for params here, it will be passed by crud
 */
public class ScSeekerIntimity extends UiAnchoret<ScSeeker> implements ScSeeker {

    private transient final ActionStub stub = Ut.singleton(ActionService.class);

    @Override
    public Future<JsonObject> fetchImpact(final JsonObject params) {
        /*
         * Uri, Method
         */
        final String uri = params.getString(ScSeeker.ARG0);
        final HttpMethod method = HttpMethod.valueOf(params.getString(ScSeeker.ARG1));
        final String sigma = params.getString(ScSeeker.ARG2);
        LOG.Resource.info(this.getLogger(), ScAuthMsg.SEEKER_RESOURCE, uri, method, sigma);
        return this.stub.fetchAction(uri, method, sigma).compose(action -> Objects.isNull(action) ?
            FnVertx.failOut(_80209Exception404ActionMissing.class, method + " " + uri) :
            UObject.create(params).append(KName.RESOURCE_ID, action.getResourceId())
                .toFuture());
    }
}
