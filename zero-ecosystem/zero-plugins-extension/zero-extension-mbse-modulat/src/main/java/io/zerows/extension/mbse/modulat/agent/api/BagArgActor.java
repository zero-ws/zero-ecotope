package io.zerows.extension.mbse.modulat.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Me;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.extension.mbse.modulat.agent.service.BagArgStub;
import io.zerows.extension.mbse.modulat.eon.Addr;
import io.zerows.unity.Ux;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class BagArgActor {

    @Inject
    private transient BagArgStub bagArgStub;

    @Address(Addr.Argument.BAG_ARGUMENT)
    public Future<JsonObject> fetchBag(final String bagAbbr) {
        return this.bagArgStub.fetchBagConfig(bagAbbr);
    }

    @Address(Addr.Argument.BAG_ARGUMENT_VALUE)
    public Future<JsonObject> fetchBagData(final String bagAbbr) {
        return this.bagArgStub.fetchBag(bagAbbr);
    }

    @Address(Addr.Argument.BAG_CONFIGURE)
    @Me
    public Future<JsonObject> saveBag(final String bagId, final JsonObject request,
                                      final User user) {
        final String userKey = Ux.keyUser(user);
        request.put(KName.UPDATED_BY, userKey);
        return this.bagArgStub.saveBag(bagId, request);
    }
}
