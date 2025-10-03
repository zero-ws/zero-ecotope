package io.zerows.extension.mbse.modulat.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.mbse.modulat.agent.service.BagArgStub;
import io.zerows.extension.mbse.modulat.eon.Addr;
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
