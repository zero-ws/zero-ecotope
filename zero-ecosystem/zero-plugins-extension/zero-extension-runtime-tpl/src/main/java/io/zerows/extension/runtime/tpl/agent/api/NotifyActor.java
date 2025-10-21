package io.zerows.extension.runtime.tpl.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Me;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.runtime.tpl.agent.service.NotifyStub;
import io.zerows.extension.runtime.tpl.eon.Addr;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

/**
 * @author lang : 2024-04-02
 */
@Queue
public class NotifyActor {

    @Inject
    private NotifyStub notifyStub;


    @Address(Addr.Notify.MY_FETCH)
    @Me
    public Future<JsonObject> saveNotify(final String userKey,
                                         final JsonObject data,
                                         final User user, final XHeader header) {
        data.put(KName.APP_ID, header.getAppId());
        data.put(KName.SIGMA, header.getSigma());
        data.put(KName.LANGUAGE, header.getLanguage());

        Ke.umCreatedJ(data, user);
        return this.notifyStub.saveNotify(OwnerType.USER, userKey, data)
            .compose(Ux::futureJ);
    }

    @Address(Addr.Notify.MY_SAVE)
    public Future<JsonObject> fetchNotify(final String user) {
        return this.notifyStub.fetchNotify(OwnerType.USER, user)
            .compose(Ux::futureJ);
    }
}
