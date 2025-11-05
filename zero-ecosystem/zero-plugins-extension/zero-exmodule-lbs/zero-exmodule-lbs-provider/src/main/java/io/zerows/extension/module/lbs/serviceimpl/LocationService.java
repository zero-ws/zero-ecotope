package io.zerows.extension.module.lbs.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.lbs.domain.tables.daos.LLocationDao;
import io.zerows.extension.module.lbs.servicespec.LocationStub;
import io.zerows.program.Ux;

public class LocationService implements LocationStub {

    @Override
    public Future<JsonObject> fetchAsync(final String locationId) {
        return DB.on(LLocationDao.class)
            .fetchByIdAsync(locationId)
            .compose(Ux::futureJ);
    }
}
