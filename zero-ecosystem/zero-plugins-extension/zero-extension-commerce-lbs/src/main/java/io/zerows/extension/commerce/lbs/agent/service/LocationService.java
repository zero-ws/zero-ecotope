package io.zerows.extension.commerce.lbs.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LLocationDao;
import io.zerows.program.Ux;

public class LocationService implements LocationStub {

    @Override
    public Future<JsonObject> fetchAsync(final String locationId) {
        return Ux.Jooq.on(LLocationDao.class)
            .fetchByIdAsync(locationId)
            .compose(Ux::futureJ);
    }
}
